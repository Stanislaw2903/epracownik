import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EbokSharedModule } from 'app/shared/shared.module';
import { InvoicesComponent } from './invoices.component';
import { InvoicesDetailComponent } from './invoices-detail.component';
import { InvoicesUpdateComponent } from './invoices-update.component';
import { InvoicesDeleteDialogComponent } from './invoices-delete-dialog.component';
import { invoicesRoute } from './invoices.route';
import { TableModule } from 'primeng/table';

@NgModule({
  imports: [EbokSharedModule, RouterModule.forChild(invoicesRoute), TableModule],
  declarations: [InvoicesComponent, InvoicesDetailComponent, InvoicesUpdateComponent, InvoicesDeleteDialogComponent],
  entryComponents: [InvoicesDeleteDialogComponent],
})
export class EbokInvoicesModule {}
