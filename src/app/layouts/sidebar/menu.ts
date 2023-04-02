import { MenuItem } from './menu.model';

export const MENU_CLIENT: MenuItem[] = [
  {
    id: 1,
    label: 'Menu',
    isTitle: true
  },
  {
    id: 2,
    label: 'Profile',
    icon: 'mdi mdi-account-circle',
    link: '/client/profile'
  },
  {
    id: 3,
    label: 'Schedule',
    icon: 'mdi mdi-calendar-check-outline',
    link: '/client/schedule'
  },
  {
    id: 4,
    label: 'Services',
    icon: 'ri-list-settings-line',
    link: '/client/services'
  },
  {
    id: 5,
    label: 'Products',
    icon: 'ri-apps-line',
    link: '/client/products'
  }
];

export const MENU_EMPLOYEE: MenuItem[] = [
  {
    id: 1,
    label: 'Menu',
    isTitle: true
  },
  {
    id: 2,
    label: 'Schedule',
    icon: 'ri-pages-line',
    link: '/schedule'
  },
  {
    id: 3,
    label: 'Bookings',
    icon: 'ri-pages-line',
    link: '/bookings'
  },
  {
    id: 4,
    label: 'Cash Up',
    icon: 'ri-wallet-2-line',
    link: '/cash-up'
  },
  {
    id: 5,
    label: 'Services',
    icon: 'ri-list-settings-line',
    link: '/services'
  },
  {
    id: 6,
    label: 'Products',
    icon: 'ri-apps-line',
    link: '/products'
  },
  {
    id: 7,
    label: 'Hr',
    icon: 'ri-user-settings-line',
    link: '/hr'
  },
  {
    id: 8,
    label: 'Clients',
    icon: 'ri-team-line',
    link: '/clients'
  },
  {
    id: 9,
    label: 'Settings',
    icon: 'ri-settings-5-line',
    link: '/settings'
  },
  {
    id: 10,
    label: 'Marketing',
    icon: 'ri-pages-line',
    link: '/marketing'
  },
  {
    id: 11,
    label: 'Reporting',
    icon: 'ri-line-chart-line',
    link: '/reporting'
  },
  {
    id: 12,
    label: 'Analytics',
    icon: 'ri-bar-chart-grouped-line',
    link: '/analytics'
  },
];
