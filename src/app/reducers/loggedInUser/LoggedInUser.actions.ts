import { createAction, props } from '@ngrx/store';

export interface LoggedInUser {
  loggedInUser: {
    token: string,
    fullName: string,
    role: string,
    userPermissionList: any[],
    hasAcceptedTerms: boolean
  }
}

export const addLoggedInUser = createAction('addLoggedInUser', props<LoggedInUser>());
