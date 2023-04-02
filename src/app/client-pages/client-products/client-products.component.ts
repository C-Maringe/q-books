import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

interface ProductsItems {
  price: number | null,
  special: boolean,
  specialEndDate: number,
  specialPrice: number | null,
  productDescription: string,
  productName: string,
  category: string,
  active: boolean,
  id: string
}

interface SearchForm {
  search: string
}

interface ItemData {
  id: number;
  name: string;
  age: number;
  address: string;
}

@Component({
  selector: 'app-client-products',
  templateUrl: './client-products.component.html',
  styleUrls: ['./client-products.component.scss']
})
export class ClientProductsComponent implements OnInit {

  constructor(
    private modalService: NgbModal,
    private apiService: ApiService,
    private notification: NzNotificationService) { }

  form: SearchForm = { search: '' }

  products: ProductsItems = {
    price: null,
    special: false,
    specialEndDate: 0,
    specialPrice: null,
    productDescription: '',
    productName: '',
    category: '',
    active: false,
    id: ''
  }

  specialEndDate = Date.now();

  receivedMessage = ''

  placement = 'topLeft';

  initialproducts = this.products;

  SuccessNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      this.receivedMessage, '',
      { nzPlacement: position }
    );
  }

  LoginFailedNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      this.receivedMessage, '',
      { nzPlacement: position }
    );
  }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});

  FilterTableDataItems: any[] = []

  fetchProducts() {
    this.apiService.get('/api/mobile/schedule/products')
      .then(response => {
        this.GridJs = response;
        this.TableData = [...this.GridJs];
        this.FilterTableDataItems = Array.from(new Set(this.TableData.map(item => item.category)))
          .map(category => this.TableData.find(item => item.category === category)).map(item => item.category)
        this.column = Object.keys(this.GridJs[0] || {});
      })
      .catch(error => console.log(error));
  }

  productsFilterValue: any = 'Filter by Category';

  productsFilterCategory(data: string) {
    this.productsFilterValue = data;
    this.handleTableDataFilter();
  }

  handleTableDataFilter() {
    if (this.productsFilterValue !== 'Filter by Category') {
      this.TableData = this.GridJs.filter(
        (data: { category: string; }) => data.category === this.productsFilterValue)
    }
    else {
      this.TableData = this.GridJs
    }
  }

  editProduct: boolean = false;

  productEditSelect(data: any): void {
    this.editProduct = true;
    this.products = data;
    document.getElementById('open-product-modal')?.click()
  }

  onCancelModal() {
    this.editProduct = false;
    this.products = this.initialproducts;
  }

  ngOnInit() {
    this.fetchProducts()
  }

  onValueChange() {
    this.TableData = this.GridJs.filter((data: any) =>
      this.column.some((column) =>
        data[column] === null ? "" : data[column].toString()
          .toLowerCase().indexOf(this.form.search.toString().toLowerCase()) > -1
      )
    )
  }
  listOfCurrentPageData: readonly ItemData[] = [];

  onCurrentPageDataChange($event: readonly ItemData[]): void {
    this.listOfCurrentPageData = $event;
  }

  fullLGModal(fullMdDataModal: any) {
    this.modalService.open(fullMdDataModal, { size: 'lg', centered: true, backdrop: 'static' });
  }

  isVisible = false;
  isOkLoading = false;

  showModal(): void {
    this.isVisible = true;
  }

  handleOk(): void {
    this.isOkLoading = true;
    setTimeout(() => {
      this.isVisible = false;
      this.isOkLoading = false;
    }, 3000);
  }

  handleCancel(): void {
    this.isVisible = false;
  }

}
