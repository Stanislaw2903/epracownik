import { Component, Renderer2, OnDestroy } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';

import { LoginService } from 'app/core/login/login.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';

@Component({
  selector: 'jhi-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['login-page.scss'],
})
export class LoginPageComponent implements OnDestroy {
  authenticationError = false;
  focus = false;

  loginForm = this.fb.group({
    username: [''],
    password: [''],
    rememberMe: [false],
  });

  constructor(
    private eventManager: JhiEventManager,
    private loginService: LoginService,
    private stateStorageService: StateStorageService,
    private router: Router,
    private fb: FormBuilder,
    private renderer: Renderer2
  ) {
    this.renderer.addClass(document.body, 'login');
  }

  ngOnDestroy(): void {
    this.renderer.removeClass(document.body, 'login');
  }

  login(): void {
    this.loginService
      .login({
        username: this.loginForm.get('username')!.value,
        password: this.loginForm.get('password')!.value,
        rememberMe: this.loginForm.get('rememberMe')!.value,
      })
      .subscribe(
        () => {
          this.authenticationError = false;
          if (
            this.router.url === '/account/register' ||
            this.router.url.startsWith('/account/activate') ||
            this.router.url.startsWith('/account/reset/')
          ) {
            this.router.navigate(['']);
          }

          this.eventManager.broadcast({
            name: 'authenticationSuccess',
            content: 'Sending Authentication Success',
          });

          // previousState was set in the authExpiredInterceptor before being redirected to login modal.
          // since login is successful, go to stored previousState and clear previousState
          const redirect = this.stateStorageService.getUrl();
          // TODO Check if the redirect would be login again and route to home
          if (redirect) {
            this.stateStorageService.storeUrl('null');
            this.router.navigateByUrl(redirect);
          } else {
            this.router.navigate(['dashboard']);
          }
        },
        () => (this.authenticationError = true)
      );
  }

  register(): void {
    this.router.navigate(['/account/register']);
  }

  requestResetPassword(): void {
    this.router.navigate(['/account/reset', 'request']);
  }
}
