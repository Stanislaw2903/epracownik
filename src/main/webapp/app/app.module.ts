import { JhiToastModule } from 'app/shared/toast/toast.module';
import { NgModule } from '@angular/core';
import { LoginPageModule } from './login/login-page.module';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import './vendor';
import { EbokSharedModule } from 'app/shared/shared.module';
import { EbokCoreModule } from 'app/core/core.module';
import { EbokAppRoutingModule } from './app-routing.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
import { RippleModule } from 'primeng/ripple';
import { DialogModule } from 'primeng/dialog';
import { PanelMenuModule } from 'primeng/panelmenu';
import { CardModule } from 'primeng/card';
import { BreadcrumbModule } from 'primeng/breadcrumb';

import { VisitorComponent } from './layouts/visitor/visitor.component';
import { LoggedComponent } from './layouts/logged/logged.component';
import { SidebarComponent } from './layouts/sidebar/sidebar.component';
import { ScrollPanelModule } from 'primeng/scrollpanel';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    EbokSharedModule,
    EbokCoreModule,
    RippleModule,
    LoginPageModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    EbokAppRoutingModule,
    JhiToastModule,
    DialogModule,
    PanelMenuModule,
    CardModule,
    ScrollPanelModule,
    BreadcrumbModule,
  ],
  declarations: [
    MainComponent,
    NavbarComponent,
    ErrorComponent,
    PageRibbonComponent,
    ActiveMenuDirective,
    FooterComponent,
    VisitorComponent,
    LoggedComponent,
    SidebarComponent,
  ],
  bootstrap: [MainComponent],
})
export class EbokAppModule {}
