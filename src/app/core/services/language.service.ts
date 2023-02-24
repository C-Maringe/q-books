import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Injectable({ providedIn: 'root' })
export class LanguageService {

  public languages: string[] = ['en', 'es', 'de', 'it', 'ru'];

  constructor(private cookieService: CookieService) {

    let browserLang: any;
    /***
     * cookie Language Get
    */
  }

  /***
   * Cookie Language set
   */
  public setLanguage(lang: any) {
    this.cookieService.set('lang', lang);
  }

}
