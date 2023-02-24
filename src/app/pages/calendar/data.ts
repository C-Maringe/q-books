import { EventInput } from '@fullcalendar/angular';

let eventGuid = 0;
export function createEventId() {
    return String(eventGuid++);
}

const category = [
    {
        name: '--Select--',
        value: '',
        option: "selected"
    },
    {
      name: 'Danger',
      value: 'bg-danger',
      option: ""
    },
    {
        name: 'Success',
        value: 'bg-success',
        option: ""
    },
    {
        name: 'Primary',
        value: 'bg-primary',
        option: ""
    },
    {
        name: 'Info',
        value: 'bg-info',
        option: ""
    },
    {
        name: 'Dark',
        value: 'bg-dark',
        option: ""
    },
    {
        name: 'Warning',
        value: 'bg-warning',
        option: ""
    }
];
var date = new Date();
var d = date.getDate();
var m = date.getMonth();
var y = date.getFullYear();
const calendarEvents: EventInput[] = [
    {
        id: createEventId(),
        title: 'All Day Event',
        start: new Date(y, m, 1),
        className: 'bg-soft-primary'
    },
    {
        id: createEventId(),
        title: 'Visit Online Course',
        start: new Date(y, m, d - 3, 16, 0),
        className: 'bg-soft-warning',
    },
    {
        id: createEventId(),
        title: 'World Leprosy Day',
        start: new Date(y, m, d - 3, 16, 0),
        allDay: false,
        className: 'bg-soft-info',
    },
    {
        id: createEventId(),
        title: 'Meeting With Designer',
        start: new Date(y, m, d, 10, 30),
        allDay: false,
        className: 'bg-soft-success',
    },
    {
        id: createEventId(),
        title: 'Birthday Party',
        start: new Date(y, m, d + 1, 19, 0),
        end: new Date(y, m, d + 1, 22, 30),
        allDay: false,
        className: 'bg-soft-success'
    },
    {
        id: createEventId(),
        title: 'Repeating Event',
        start: new Date(y, m, d + 4, 19, 0),
        end: new Date(y, m, d + 9, 22, 30),
        allDay: false,
        className: 'bg-soft-primary',
    },
    {
        id: createEventId(),
        title: 'Weekly Strategy Planning',
        start: new Date(y, m, d + 9, 19, 0),
        end: new Date(y, m, d + 10, 22, 30),
        className: 'bg-soft-danger'
    },
    {
        id: createEventId(),
        title: 'International Mother Language Day',
        start: new Date(y, m, d + 19, 16, 0),
        allDay: false,
        className: 'bg-soft-info',
    },
    {
        id: createEventId(),
        title: 'World Thinking Day',
        start: new Date(y, m, d + 20, 16, 0),
        allDay: false,
        className: 'bg-soft-info',
    },
    {
        id: createEventId(),
        title: 'Client Meeting with Alexis',
        start: new Date(y, m, d + 22, 19, 0),
        end: new Date(y, m, d + 24, 22, 30),
        className: 'bg-soft-danger'
    },
    {
        id: createEventId(),
        title: 'Velzon Project Discussion with Team',
        start: new Date(y, m, d + 23, 16, 0),
        end: new Date(y, m, d + 24, 22, 30),
        allDay: false,
        className: 'bg-soft-info',
    },
    {
        id: createEventId(),
        title: 'Click for Google',
        start: new Date(y, m, d + 26, 16, 0),
        allDay: false,
        className: 'bg-soft-dark',
    },
    {
        id: createEventId(),
        title: `International Women's Day`,
        start: new Date(y, m, d + 34, 16, 0),
        allDay: false,
        className: 'bg-soft-info',
    },
    
];

export { category, calendarEvents };
