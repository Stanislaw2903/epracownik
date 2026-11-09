import { Component, OnInit } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { LoginService } from 'app/core/login/login.service';
import { filter } from 'rxjs/operators';
import { ProfileService } from '../profiles/profile.service';

interface MenuOptions {
  invoices: boolean;
  payroll: boolean;
  personnelData: boolean;
  otherAbsences: boolean;
  administration: boolean;
  system: boolean;
  dashboard?: boolean;
}

@Component({
  selector: 'jhi-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {
  inProduction?: boolean;
  isSidebarCollapsed = true;
  swaggerEnabled?: boolean;
  menuOptions: MenuOptions = {
    invoices: false,
    payroll: false,
    personnelData: false,
    otherAbsences: false,
    administration: false,
    system: false,
  };

  constructor(private loginService: LoginService, private profileService: ProfileService, private router: Router) {}

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
    });

    this.router.events.pipe(filter(event => event instanceof NavigationStart)).subscribe((event: any) => {
      if (event.url !== '/') {
        this.isSidebarCollapsed = true;
      }
    });
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  toggle(param: keyof MenuOptions): void {
    // Sprawdzamy, czy opcja, którą klikamy, jest już ustawiona na 'true'
    if (this.menuOptions[param]) {
      // Jeśli tak, resetujemy wszystkie opcje na 'false'
      this.menuOptions = {
        invoices: false,
        payroll: false,
        personnelData: false,
        otherAbsences: false,
        administration: false,
        system: false,
      };
    } else {
      // Jeśli nie, ustawiamy tylko tę opcję na 'true', reszta jest 'false'
      this.menuOptions = {
        invoices: false,
        payroll: false,
        personnelData: false,
        otherAbsences: false,
        administration: false,
        system: false,
      };
      this.menuOptions[param] = true;
    }
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  getSubmenuHeight(menu: any, type: keyof MenuOptions): number {
    return menu.parentNode.classList.contains('active') || this.menuOptions[type] ? menu.scrollHeight : 0;
  }
}
