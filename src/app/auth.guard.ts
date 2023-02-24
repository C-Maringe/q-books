import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { Store } from '@ngrx/store';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { Observable } from 'rxjs';
import { AuthServiceData } from './reducers/authService/auth-service.reducer';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  isLoggedIn$: boolean = false;

  constructor(
    private store: Store,
    private router: Router,
    private notification: NzNotificationService) {
    store.select(AuthServiceData).subscribe((value: boolean) => {
      this.isLoggedIn$ = value
    })
  }

  receivedMessage = ''

  placement = 'topRight';

  PageAccessFailedNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      'login to access this page', '',
      { nzPlacement: position }
    );
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.isLoggedIn$) {
      return true;
    } else {
      this.receivedMessage = 'login to access this page'
      setTimeout(() => {
        this.PageAccessFailedNotification('error', 'topRight')
      }, 200)
      this.router.navigate(['/login']);
      return false;
    }
  }
}
