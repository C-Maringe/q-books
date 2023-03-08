import { combineReducers } from '@ngrx/store';
import { AuthServiceReducer, metaReducerAuthServiceLocalStorage } from './authService/auth-service.reducer';
import { LoggedInUserReducer, metaReducerLoggedInUserLocalStorage } from './loggedInUser/LoggedInUser.reducer';
import { metaReducerTokenLocalStorage, tokenReducer } from './token/token.reducer';

export const reducers = {
  tokenmake: tokenReducer,
  isLoggedInmake: AuthServiceReducer,
  loggedInUsermake: LoggedInUserReducer
}

export const metaReducer = {
  metaReducers: [
    metaReducerTokenLocalStorage,
    metaReducerAuthServiceLocalStorage,
    metaReducerLoggedInUserLocalStorage
  ]
}

export const rootReducer = combineReducers(reducers);

