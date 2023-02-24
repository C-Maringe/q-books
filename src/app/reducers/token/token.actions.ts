import { createAction, props } from '@ngrx/store';

export interface Token {
  token: string
}

export const addToken = createAction('addToken', props<Token>());
