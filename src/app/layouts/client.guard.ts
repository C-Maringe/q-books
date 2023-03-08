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
import { LoggedInUserData } from '../reducers/loggedInUser/LoggedInUser.reducer';

@Injectable({
  providedIn: 'root'
})
export class ClientGuard implements CanActivate {
  IsClient$: any;

  constructor(
    private store: Store,
    private router: Router,
    private notification: NzNotificationService) {
    store.select(LoggedInUserData).subscribe((value: any) => {
      this.IsClient$ = value
    })
  }

  receivedMessage = ''

  placement = 'topRight';

  PageAccessFailedNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      this.receivedMessage, '',
      { nzPlacement: position }
    );
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.IsClient$.role === 'client') {
      return true;
    } else {
      this.receivedMessage = 'You are not allowed to access this page'
      setTimeout(() => {
        this.PageAccessFailedNotification('error', 'topRight')
      }, 200)
      this.router.navigate(['/schedule']);
      return false;
    }
  }
}
