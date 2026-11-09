import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private _messageStatus$: Subject<object> = new Subject();

  public sendMessage(messageObject: any): void {
    this._messageStatus$.next(messageObject);
  }

  public getMessage(): Observable<object> {
    return this._messageStatus$.asObservable();
  }
}
