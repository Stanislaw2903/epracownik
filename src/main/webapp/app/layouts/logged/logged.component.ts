import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MenuItem } from 'primeng/api';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'jhi-logged',
  templateUrl: './logged.component.html',
  styleUrls: ['./logged.component.scss'],
})
export class LoggedComponent implements OnInit {
  static readonly ROUTE_DATA_BREADCRUMB = 'pageTitle';
  public cardException = false;
  public menuItems: MenuItem[] = [];
  readonly home = { icon: 'pi pi-home', url: 'dashboard' };

  constructor(private router: Router, private activeRoute: ActivatedRoute, private translateService: TranslateService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.cardException = this.getCardException(this.router.routerState.snapshot.root);
      }
    });
  }

  private getCardException(routeSnapshot: ActivatedRouteSnapshot): boolean {
    let cardException: boolean = routeSnapshot.data && routeSnapshot.data['cardException'] ? routeSnapshot.data['cardException'] : '';
    if (routeSnapshot.firstChild) {
      cardException = this.getCardException(routeSnapshot.firstChild) || cardException;
    }
    return cardException;
  }

  private createBreadcrumbs(route: ActivatedRoute, breadcrumbs: MenuItem[] = []): MenuItem[] {
    const children: ActivatedRoute[] = route.children;

    if (children.length === 0) {
      return breadcrumbs;
    }

    for (const child of children) {
      const routeLabel = child.snapshot.data[LoggedComponent.ROUTE_DATA_BREADCRUMB];
      if (!(routeLabel === null || routeLabel === undefined)) {
        this.translateService.get(routeLabel).subscribe(label => {
          breadcrumbs.push({ label });
        });
      }

      return this.createBreadcrumbs(child, breadcrumbs);
    }
    return breadcrumbs;
  }

  ngOnInit(): void {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => (this.menuItems = this.createBreadcrumbs(this.activeRoute.root)));
  }
}
