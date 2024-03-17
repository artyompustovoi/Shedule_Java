package Artyom_Pustovoy;

import java.time.LocalDate;
import java.util.*;

public class Schedule {
    private final Map<LocalDate, List<Event>> eventsMap;

    public Schedule() {
        this.eventsMap = new HashMap<>();
    }

    public Schedule(Collection<Event> events) {
        this();
        for (Event event : events) {
            insert(event);
        }
    }

    public Schedule insert(Event e) {
        eventsMap.computeIfAbsent(e.date(), k -> new ArrayList<>()).add(e);
        return this;
    }

    public Optional<Event> get(LocalDate d, String title) {
        return eventsMap.getOrDefault(d, Collections.emptyList())
                .stream()
                .filter(event -> event.title().equals(title))
                .findFirst();
    }

    public List<Event> exportAll() {
        List<Event> allEvents = new ArrayList<>();
        eventsMap.values().forEach(allEvents::addAll);
        allEvents.sort(Comparator.comparing(Event::date));
        return Collections.unmodifiableList(allEvents);
    }

    public List<Event> exportDateRange(LocalDate fromIncluding, LocalDate toIncluding) {
        List<Event> eventsInRange = new ArrayList<>();
        for (LocalDate date = fromIncluding; !date.isAfter(toIncluding); date = date.plusDays(1)) {
            List<Event> eventsOnDate = eventsMap.getOrDefault(date, Collections.emptyList());
            eventsInRange.addAll(eventsOnDate);
        }
        eventsInRange.sort(Comparator.comparing(Event::date));
        return Collections.unmodifiableList(eventsInRange);
    }

    public List<Event> exportTitle(String title) {
        List<Event> eventsWithTitle = new ArrayList<>();
        eventsMap.values().forEach(events -> events.stream()
                .filter(event -> event.title().equals(title))
                .forEach(eventsWithTitle::add));
        eventsWithTitle.sort(Comparator.comparing(Event::date));
        return Collections.unmodifiableList(eventsWithTitle);
    }

    public Optional<Event> remove(LocalDate d, String title) {
        List<Event> eventsOnDate = eventsMap.get(d);
        if (eventsOnDate != null) {
            Iterator<Event> iterator = eventsOnDate.iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                if (event.title().equals(title)) {
                    iterator.remove();
                    if (eventsOnDate.isEmpty()) {
                        eventsMap.remove(d);
                    }
                    return Optional.of(event);
                }
            }
        }
        return Optional.empty();
    }

    public List<Event> removeDateRange(LocalDate fromIncluding, LocalDate toIncluding) {
        List<Event> removedEvents = new ArrayList<>();
        for (LocalDate date = fromIncluding; !date.isAfter(toIncluding); date = date.plusDays(1)) {
            List<Event> eventsOnDate = eventsMap.remove(date);
            if (eventsOnDate != null) {
                removedEvents.addAll(eventsOnDate);
            }
        }
        removedEvents.sort(Comparator.comparing(Event::date));
        return Collections.unmodifiableList(removedEvents);
    }

    public List<Event> removeTitle(String title) {
        List<Event> removedEvents = new ArrayList<>();
        for (List<Event> eventsOnDate : eventsMap.values()) {
            Iterator<Event> iterator = eventsOnDate.iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                if (event.title().equals(title)) {
                    iterator.remove();
                    removedEvents.add(event);
                }
            }
        }
        removedEvents.sort(Comparator.comparing(Event::date));
        return Collections.unmodifiableList(removedEvents);
    }

    public List<Event> removeAll() {
        List<Event> allEvents = exportAll();
        eventsMap.clear();
        return allEvents;
    }
}
