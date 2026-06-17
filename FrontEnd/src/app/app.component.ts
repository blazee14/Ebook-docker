import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { AuthService, UserData } from './services/auth.service';
import { CartService } from './services/cart.service';
import { ChatbotComponent } from './features/chatbot/chatbot.component';

export interface AuthState {
  isLoggedIn: boolean;
  isAdmin: boolean;
  user: UserData | null;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, RouterOutlet, RouterLink,
    MatToolbarModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatBadgeModule, MatDividerModule,
    ChatbotComponent
    // FormsModule y MatIconModule eliminados — no se usan en este componente
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {
  // Un solo observable consolidado en lugar de tres separados
  authState$: Observable<AuthState>;
  cartItemCount$: Observable<number>;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {
    this.authState$ = this.authService.currentUser.pipe(
      map(user => ({
        isLoggedIn: !!user,
        isAdmin: user?.roles?.includes('ADMIN') ?? false,
        user,
      })),
      takeUntil(this.destroy$)
    );

    this.cartItemCount$ = this.cartService.getCartItemCount().pipe(
      takeUntil(this.destroy$)
    );
  }

  openMenu(trigger: MatMenuTrigger): void {
    trigger.openMenu();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}