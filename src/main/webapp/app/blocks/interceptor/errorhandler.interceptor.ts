import { Injectable } from '@angular/core';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { HttpInterceptor, HttpRequest, HttpErrorResponse, HttpHandler } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { ToastService } from 'app/shared/toast/toast.service';

@Injectable()
export class ErrorHandlerInterceptor implements HttpInterceptor {
  constructor(private toastService: ToastService, private eventManager: JhiEventManager) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
    return next.handle(request).pipe(
      tap(null, (err: HttpErrorResponse) => {
        if (!(err.status === 401 && (err.message === '' || (err.url && err.url.includes('api/account'))))) {
          this.eventManager.broadcast(new JhiEventWithContent('ebokApp.httpError', err));
        }
      }),
      catchError(error => this.errorHandler(error))
    );
  }

  private errorHandler(response: any): Observable<any> {
    switch (response.status) {
      case 500:
        this.toastService.sendMessage({
          type: 'error',
          title: 'error.title',
          message: 'error.http.500',
          error: response.error || null,
        });
        break;
      case 404:
        this.toastService.sendMessage({
          type: 'error',
          title: 'error.title',
          message: 'error.http.404',
          error: response.error || null,
        });
        break;
      case 418:
        this.toastService.sendMessage({
          type: 'error',
          title: response.error.error.title,
          message: 'error.' + response.error.error.message,
          error: response.error || null,
        });
        break;
      default:
        if (response.error.message && response.error.message.indexOf('.') !== -1) {
          this.toastService.sendMessage({
            type: 'error',
            title: response.error.error,
            message: response.error.message,
            error: response.status === 400 ? response.error : null,
          });
        }
        break;
    }
    throw response;
  }
}
