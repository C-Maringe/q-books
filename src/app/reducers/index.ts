import { combineReducers } from '@ngrx/store';
import { AuthServiceReducer, metaReducerAuthServiceLocalStorage } from './authService/auth-service.reducer';
import { metaReducerTokenLocalStorage, tokenReducer } from './token/token.reducer';

export const reducers =
{
  tokenmake: tokenReducer,
  isLoggedInmake: AuthServiceReducer,
}

export const metaReducer = {
  metaReducers: [
    metaReducerTokenLocalStorage,
    metaReducerAuthServiceLocalStorage
  ]
}

export const rootReducer = combineReducers(reducers);

