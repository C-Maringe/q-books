import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { loadFull } from "tsparticles";
import { ClickMode, HoverMode, MoveDirection, OutMode, Container, Engine } from 'tsparticles-engine';
import { ApiService } from 'src/app/core/services/api.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';

interface LoginForm {
  emailAddress: String,
}

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  receivedMessage = ''

  SuccessNotification(type: string): void {
    this.notification.create(
      type,
      this.receivedMessage, ''
    );
  }

  LoginFailedNotification(type: string): void {
    this.notification.create(
      type,
      this.receivedMessage, ''
    );
  }

  year: number = new Date().getFullYear();

  constructor(
    private router: Router,
    private apiService: ApiService,
    private notification: NzNotificationService,
    private formBuilder: UntypedFormBuilder) { }

  form: LoginForm = {
    emailAddress: '',
  }

  loginform!: UntypedFormGroup;

  ngOnInit(): void {
    this.loginform = this.formBuilder.group({
      emailAddress: ['', [Validators.required, Validators.email]],
    });
  }

  fieldTextType = false

  HandleLogin() {
    if (this.form.emailAddress != '') {
      this.apiService.put('/api/public/web/forgot-password', this.form)
        .then(data => {
          this.receivedMessage = data.message
          this.SuccessNotification('success')
          setTimeout(() => {
            this.router.navigate(['/login'])
          }, 2000)
        })
        .catch(error => {
          if (error.error.detail === undefined) {
            this.receivedMessage = 'Server Error'
          }
          else {
            this.receivedMessage = error.error.detail
          }
          setTimeout(() => {
            this.LoginFailedNotification('error')
          }, 200)
        })
    }
    else {
      alert("Invalid details Entered")
    }
  }

  id = "tsparticles";

  particlesUrl = "http://foo.bar/particles.json";

  particlesOptions = {
    fpsLimit: 150,
    interactivity: {
      events: {
        onClick: {
          enable: true,
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
