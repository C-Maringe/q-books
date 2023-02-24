import { ActionReducer, createFeatureSelector, createReducer, createSelector, INIT, on, UPDATE } from '@ngrx/store';
import { Token, addToken } from './token.actions';

export const initialTokenState: Token[] = [{ token: '' }];

export const tokenReducer = createReducer(
  initialTokenState,
  on(addToken, (entries, token) => {
    const newToken: Token[] = JSON.parse(JSON.stringify([token]));
    return newToken;
  })
);

export const tokenData = createSelector(
  createFeatureSelector('tokenmake'),
  (state: Token[]) => {
    return state[0].token
  }
)

export const metaReducerTokenLocalStorage = (reducer: ActionReducer<any>): ActionReducer<any> => {
  return (state, action) => {
    if (action.type === INIT || action.type === UPDATE) {
      const storageValue = localStorage.getItem("token");
      if (storageValue) {
        try {
          return JSON.parse(storageValue)
        } catch (error) {
          localStorage.removeItem("token")
        }
      }
    }
    const nextState = reducer(state, action);
    localStorage.setItem("token", JSON.stringify(nextState))
    return nextState
  }
}
