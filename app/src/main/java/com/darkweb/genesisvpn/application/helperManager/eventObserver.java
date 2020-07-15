package com.darkweb.genesisvpn.application.helperManager;
import com.darkweb.genesisvpn.application.constants.enums;
import java.util.List;

public class eventObserver {
    public interface eventListener
    {
        void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type);
    }
}
