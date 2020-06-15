package danilov.roman.bookingMeetingRoom.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import danilov.roman.bookingMeetingRoom.entity.google.GoogleCalendarEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Class for send events to google calendar
 */
@Component
public class SynchronizeEventsWithGoogleCalendar {

    private final Logger logger = LoggerFactory.getLogger(SynchronizeEventsWithGoogleCalendar.class);

    private static final String APPLICATION_NAME = "Google Calendar API Reservations Meeting Rooms";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CALENDAR_ID = "primary";

    /**
     * Global instance of the scopes.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SynchronizeEventsWithGoogleCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

//    private HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
//        return new HttpRequestInitializer() {
//            @Override
//            public void initialize(HttpRequest httpRequest) throws IOException {
//                requestInitializer.initialize(httpRequest);
//                httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
//                httpRequest.setReadTimeout(3 * 60000);  // 3 minutes read timeout
//            }
//        };
//    }

    /**
     * Sends a list of events to the google calendar
     *
     * @param entities Events to calendar
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void googleSend(List<GoogleCalendarEntity> entities) throws IOException, GeneralSecurityException {
        for (GoogleCalendarEntity entity : entities) {
            Event event = new Event()
                    .setSummary(entity.getTitle())
                    .setDescription(entity.getDesc());

            EventDateTime start = new EventDateTime()
                    .setDateTime(entity.getStartDateTime())
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(entity.getEndDateTime())
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setEnd(end);

            // Build a new authorized API client service.
            Calendar service = getCalendarService();
            event = service.events().insert(CALENDAR_ID, event).execute();
            logger.info(String.format("Event created: %s", event.getHtmlLink()));
        }
    }
}
