import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, ViewportScroller } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';
import { AuthService } from '../../../services/auth.service';
import { Product } from '../../../models/product.model';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // <--- Importa MatSnackBarModule aquí también

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule // <--- ¡CAMBIA ESTO A MatSnackBarModule!
  ],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product$: Observable<Product | null> | null = null;
  loading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    private activatedRoute: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private snackBar: MatSnackBar,
    private viewportScroller: ViewportScroller,
    private router: Router,
    private authService: AuthService
  ) {
    // Forzar scroll al inicio inmediatamente
    window.scrollTo(0, 0);
  }

  ngOnInit(): void {
    // Asegurar que la página se desplaza al inicio cuando se carga un nuevo producto
    this.router.events.subscribe(() => {
      this.viewportScroller.scrollToPosition([0, 0]);
    });

    this.product$ = this.activatedRoute.paramMap.pipe(
      switchMap(params => {
        this.loading = true;
        this.errorMessage = null;
        const productId = params.get('id');
        if (productId) {
          return this.productService.getProductById(parseInt(productId, 10));
        }
        this.errorMessage = 'ID de producto no encontrado.';
        this.loading = false;
        return of(null);
      })
    );

    this.product$.subscribe({
      next: (product) => {
        this.loading = false;
        if (!product) {
          this.errorMessage = 'Libro no encontrado.';
        } else {
          // Forzar scroll al inicio cuando el producto se carga
          window.scrollTo(0, 0);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error al cargar los detalles del libro.';
      }
    });
  }

  addToCart(product: Product): void {
    // Mostrar mensaje si no está logueado
    // Nota: AuthService se inyecta a continuación si es necesario en constructor
    try {
      // Access AuthService via (window as any).ng if not injected? Better to inject; check constructor.
    } catch (e) {}
    // We'll inject AuthService dynamically to avoid constructor changes interfering; but simpler: import and use injection
    // If AuthService is available via injector, use it. Otherwise proceed.
    // For clarity, assume AuthService is injected as 'authService' — add it to constructor if missing.
    if ((this as any).authService && !(this as any).authService.isLoggedIn()) {
  const ref = this.snackBar.open('Para comprar un libro debes iniciar sesión.', 'Iniciar sesión', { duration: 6000 });
  ref.onAction().subscribe(() => this.router.navigate(['/login']));
      return;
    }

    this.cartService.addToCart(product).subscribe({
      next: () => {
        // El snackbar ya se muestra en cart.service
      },
      error: (err) => {
        // El snackbar ya se muestra en cart.service para errores de stock/validación
        console.error('Error al añadir producto desde ProductDetail:', err);
      }
    });
  }
}
