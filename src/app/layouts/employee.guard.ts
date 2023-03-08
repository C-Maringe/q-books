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
import { LoggedInUser } from '../reducers/loggedInUser/LoggedInUser.actions';
import { LoggedInUserData } from '../reducers/loggedInUser/LoggedInUser.reducer';

@Injectable({
  providedIn: 'root'
})
export class EmployeeGuard implements CanActivate {
  IsEmployee$: any;

  constructor(
    private store: Store,
    private router: Router,
    private notification: NzNotificationService) {
    store.select(LoggedInUserData).subscribe((value: any) => {
      this.IsEmployee$ = value
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
    if (this.IsEmployee$.role === 'employee') {
      return true;
    } else {
      this.receivedMessage = 'You are not allowed to access this page'
      setTimeout(() => {
        this.PageAccessFailedNotification('error', 'topRight')
      }, 200)
      this.router.navigate(['/client/schedule']);
      return false;
    }
  }
}
