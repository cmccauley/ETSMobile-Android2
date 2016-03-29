package ca.etsmtl.applets.etsmobile.model;

import com.google.api.services.calendar.model.Event;

import java.util.Collections;

/**
 * Created by gnut3ll4 on 27/03/16.
 */
public class GoogleEventWrapper {

    private Event event;

    public GoogleEventWrapper(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GoogleEventWrapper &&
                ((GoogleEventWrapper) o).event.getId().equals(this.event.getId());

    }

    public Event getEvent() {
        return event;
    }
}
