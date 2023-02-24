/* eslint-disable @typescript-eslint/explicit-member-accessibility */
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from "@angular/common/http";
import { Observable, retry, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { Store } from '@ngrx/store';
import { tokenData } from 'src/app/reducers/token/token.reducer';

export class ErrorDetails {
  public title: string;
  public status: 400 | undefined;
  public detail: string | undefined;
  public timestamp: Date;
  public developerMessage: string | undefined;

  public constructor(title: string, message?: string, developerMessage?: string, status?: 400) {
    this.title = title;
    this.status = status;
    this.detail = message;
    this.timestamp = new Date();
    this.developerMessage = developerMessage;
  }
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = 'http://localhost:8080';
  token$: any;

  constructor(private http: HttpClient, private store: Store) {
    store.select(tokenData).subscribe((value: any) => {
      this.token$ = value
    })
  }

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error && error.error instanceof ProgressEvent) {
      throw new ErrorDetails('A connection could not be established. Please contact an administrator.');
    }
    return throwError(ErrorDetails, error.error);
  }

  get(endpoint: string): Promise<any> {
    const headers = { 'Authorization': `Bearer ${this.token$}` };
    return this.http.get<any>(this.baseUrl + endpoint, { headers }).toPromise();
  }

  put(endpoint: string, body: any): Promise<any> {
    const headers = { 'Authorization': `Bearer ${this.token$}` };
    return this.http.put<any>(this.baseUrl + endpoint, body, { headers }).toPromise();
  }

  post(endpoint: string, body: any): Promise<any> {
    const headers = { 'Authorization': `Bearer ${this.token$}` };
    return this.http.post<any>(this.baseUrl + endpoint, body, { headers }).toPromise();
  }

  delete(endpoint: string): Promise<any> {
    const headers = { 'Authorization': `Bearer ${this.token$}` };
    return this.http.delete<any>(this.baseUrl + endpoint, { headers }).toPromise();
  }

  patch(endpoint: string, body: any): Promise<any> {
    const headers = { 'Authorization': `Bearer ${this.token$}` };
    return this.http.patch<any>(this.baseUrl + endpoint, body, { headers }).toPromise();
  }
}




