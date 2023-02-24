import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';

// Register Auth
import { Router } from '@angular/router';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';
import { loadFull } from 'tsparticles';
import { ClickMode, HoverMode, MoveDirection, OutMode, Container, Engine } from 'tsparticles-engine';

interface LoginForm {
  firstName: String,
  lastName: String,
  emailAddress: String,
  mobileNumber: String,
  password: String,
  confirmPassword: String
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  // Login Form
  signupForm!: UntypedFormGroup;
  submitted = false;
  successmsg = false;
  error = '';
  // set the current year
  year: number = new Date().getFullYear();

  ngOnInit(): void {
    /**
     * Form Validatyion
     */
    this.signupForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      first_name: ['', Validators.required],
      last_name: ['', Validators.required],
      mobile_number: ['', Validators.required],
      confirm_password: ['', Validators.required],
    });
  }

  // convenience getter for easy access to form fields
  get f() { return this.signupForm.controls; }

  receivedMessage = ''

  SuccessNotification(type: string): void {
    this.notification.create(
      type,
      'Account Created Successfully', ''
    );
  }

  LoginFailedNotification(type: string): void {
    this.notification.create(
      type,
      this.receivedMessage, ''
    );
  }

  constructor(
    private router: Router,
    private apiService: ApiService,
    private notification: NzNotificationService,
    private formBuilder: UntypedFormBuilder) { }

  loginform!: UntypedFormGroup;

  fieldTextType = false

  form: LoginForm = {
    firstName: '',
    lastName: '',
    emailAddress: '',
    mobileNumber: '',
    password: '',
    confirmPassword: ''
  }

  HandleLogin() {
    console.log(this.form)
    if (this.form.password != '' && this.form.firstName != '' && this.form.lastName != '' && this.form.emailAddress != '' && this.form.mobileNumber != '' && this.form.confirmPassword != '') {
      this.apiService.post('/api/public/web/register', this.form)
        .then(data => {
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
