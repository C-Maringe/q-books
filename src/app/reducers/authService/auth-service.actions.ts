import { createAction, props } from '@ngrx/store';

export interface AuthService {
  isLoggedIn_status: boolean;
}

export const isLoggedIn = createAction('isLoggedIn', props<AuthService>());
