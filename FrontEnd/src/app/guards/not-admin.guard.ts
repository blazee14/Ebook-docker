import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { of } from 'rxjs';

/**
 * Bloquea rutas que no deben ser accesibles por administradores.
 * Si el usuario es ADMIN lo redirige al panel de admin.
 */
export const notAdminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si el usuario está logueado y es admin, redirigir al dashboard admin
  if (authService.isLoggedIn() && authService.hasRole('ADMIN')) {
    router.navigate(['/admin']);
    return of(false);
  }

  // Permitir el acceso en cualquier otro caso (usuarios no logueados o usuarios no-admin)
  return of(true);
};
