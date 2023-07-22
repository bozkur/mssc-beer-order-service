package guru.springframework.msscbeerorderservice.web.mapper;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
public class DateMapper {
    public Timestamp offsetDateTime2TimeStamp(OffsetDateTime odt) {
        return odt == null ? null : Timestamp.from(odt.toInstant());
    }

    public OffsetDateTime timeStamp2OffsetDateTime(Timestamp timestamp) {
        return timestamp == null ? null : OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }
}
