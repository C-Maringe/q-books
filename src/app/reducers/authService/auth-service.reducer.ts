import { ActionReducer, createFeatureSelector, createReducer, createSelector, INIT, on, UPDATE } from '@ngrx/store';
import { AuthService, isLoggedIn } from './auth-service.actions';

export const initialAuthServiceState: AuthService[] = [{ isLoggedIn_status: false }];

export const AuthServiceReducer = createReducer(
  initialAuthServiceState,
  on(isLoggedIn, (entries, isLoggedIn) => {
    const newAuthService: AuthService[] = JSON.parse(JSON.stringify([isLoggedIn]));
    return newAuthService;
  })
);

export const AuthServiceData = createSelector(
  createFeatureSelector('isLoggedInmake'),
  (state: AuthService[]) => {
    return state[0].isLoggedIn_status
  }
)

export const metaReducerAuthServiceLocalStorage = (reducer: ActionReducer<any>): ActionReducer<any> => {
  return (state, action) => {
    if (action.type === INIT || action.type === UPDATE) {
      const storageValue = localStorage.getItem("isLoggedIn");
      if (storageValue) {
        try {
          return JSON.parse(storageValue)
        } catch (error) {
          localStorage.removeItem("isLoggedIn")
        }
      }
    }
    const nextState = reducer(state, action);
    localStorage.setItem("isLoggedIn", JSON.stringify(nextState))
    return nextState
  }
}
