import { Component, OnInit, ElementRef, Renderer2 } from '@angular/core';
import { Router } from '@angular/router';

import { loadFull } from "tsparticles";
import { ClickMode, HoverMode, MoveDirection, OutMode, Container, Engine } from 'tsparticles-engine';
import { ApiService } from 'src/app/core/services/api.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';

import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { tokenData } from 'src/app/reducers/token/token.reducer';
import { addToken, Token } from 'src/app/reducers/token/token.actions';

import { AuthService, isLoggedIn } from 'src/app/reducers/authService/auth-service.actions';

interface LoginForm {
  username: String,
  password: String,
  keepMeLoggedIn: boolean
}

interface TokenForm {
  token: string
}

interface IsLoggedForm {
  isLoggedIn_status: boolean
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  receivedMessage = ''

  SuccessNotification(type: string): void {
    this.notification.create(
      type,
      'Login Successfull', ''
    );
  }

  LoginFailedNotification(type: string): void {
    this.notification.create(
      type,
      this.receivedMessage, ''
    );
  }

  year: number = new Date().getFullYear();

  token$: Observable<any>;

  constructor(
    private elementRef: ElementRef, private renderer: Renderer2,
    private router: Router,
    private apiService: ApiService,
    private notification: NzNotificationService,
    private formBuilder: UntypedFormBuilder,
    private store: Store) {
    this.token$ = store.select(tokenData);
  }

  ngAfterViewInit() {
    this.renderer.setStyle(this.elementRef.nativeElement.ownerDocument.body, 'background-color', '#21D59B');
  }

  tokenParams: TokenForm = {
    token: ''
  }

  isLoggedInParams: IsLoggedForm = {
    isLoggedIn_status: false
  }

  setToken(token: Token) {
    this.store.dispatch(addToken(token));
  }

  setIsloggedIn(isLogged: AuthService) {
    this.store.dispatch(isLoggedIn(isLogged));
  }

  form: LoginForm = {
    username: '',
    password: '',
    keepMeLoggedIn: false
  }

  loginform!: UntypedFormGroup;

  ngOnInit(): void {
    this.loginform = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  fieldTextType = false

  HandleLogin() {
    if (this.form.password != '' && this.form.username != '') {
      this.apiService.put('/api/public/web/login', this.form)
        .then(data => {
          this.tokenParams.token = data.token
          this.isLoggedInParams.isLoggedIn_status = true;
          this.SuccessNotification('success')
          setTimeout(() => {
            this.setToken(this.tokenParams)
            this.setIsloggedIn(this.isLoggedInParams)
            this.router.navigate(['/schedule'])
          }, 2000)
        })
        .catch(error => {
          setTimeout(() => {
            this.LoginFailedNotification('error')
          }, 200)
          if (error.error.detail === undefined) {
            this.receivedMessage = 'Server Error'
          }
          else {
            this.receivedMessage = error.error.detail
          }
        })
    }
    else {
      this.receivedMessage = 'Invalid Credidentials Entered'
      setTimeout(() => {
        this.LoginFailedNotification('error')
      }, 200)
    }
  }

  id = "tsparticles";

  particlesUrl = "http://foo.bar/particles.json";

  particlesOptions = {
    fpsLimit: 150,
    interactivity: {
      events: {
        onClick: {
          enable: false,
          mode: ClickMode.push,
        },
        onHover: {
          enable: false,
          mode: HoverMode.repulse,
        },
        resize: true,
      },
      modes: {
        push: {
          quantity: 4,
        },
        repulse: {
          distance: 200,
          duration: 0.4,
        },
      },
    },
    particles: {
      color: {
        value: "#ffffff",
      },
      links: {
        color: "#ffffff",
        distance: 150,
        enable: false,
        opacity: 0.5,
        width: 1,
      },
      collisions: {
        enable: true,
      },
      move: {
        direction: MoveDirection.none,
        enable: true,
        outModes: {
          default: OutMode.bounce,
        },
        random: false,
        // speed: 2,
        straight: false,
      },
      number: {
        density: {
          enable: true,
          area: 800,
        },
        value: 40,
      },
      opacity: {
        value: 0.8,
      },
      shape: {
        type: "circle",
      },
      size: {
        value: { min: 1, max: 5 },
      },
    },
    detectRetina: true,
  };

  particlesLoaded(container: Container): void {
  }

  async particlesInit(engine: Engine): Promise<void> {
    await loadFull(engine);
  }
}
