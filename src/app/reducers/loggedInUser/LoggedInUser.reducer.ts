import { ActionReducer, createFeatureSelector, createReducer, createSelector, INIT, on, UPDATE } from '@ngrx/store';
import { LoggedInUser, addLoggedInUser } from './LoggedInUser.actions'

export const initialTokenState: LoggedInUser[] = [{
  loggedInUser: {
    token: '',
    fullName: '',
    role: '',
    userPermissionList: [],
    hasAcceptedTerms: false
  }
}];

export const LoggedInUserReducer = createReducer(
  initialTokenState,
  on(addLoggedInUser, (entries, loggedInUser) => {
    const newLoggedInUser: LoggedInUser[] = JSON.parse(JSON.stringify([loggedInUser]));
    return newLoggedInUser;
  })
);

export const LoggedInUserData = createSelector(
  createFeatureSelector('loggedInUsermake'),
  (state: LoggedInUser[]) => {
    return state[0]
  }
)

export const metaReducerLoggedInUserLocalStorage = (reducer: ActionReducer<any>): ActionReducer<any> => {
  return (state, action) => {
    if (action.type === INIT || action.type === UPDATE) {
      const storageValue = localStorage.getItem("loggedInUser");
      if (storageValue) {
        try {
          return JSON.parse(storageValue)
        } catch (error) {
          localStorage.removeItem("loggedInUser")
        }
      }
    }
    const nextState = reducer(state, action);
    localStorage.setItem("loggedInUser", JSON.stringify(nextState))
    return nextState
  }
}
