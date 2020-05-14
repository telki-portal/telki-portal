package bme.tmit.telki.data;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class EntryService {

	public Map<String, Long> getWeeklyCount(List<TrafficInfoEntry> entries) {

		return entries.stream().collect(groupingBy(entry -> {
			LocalDateTime entryTime = LocalDateTime.ofInstant(entry.getTime(), ZoneOffset.UTC);
			LocalDateTime weekStart = entryTime.minusDays(entryTime.getDayOfWeek().getValue() - 1);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			return weekStart.format(formatter);
		}, Collectors.counting()));

	}

}
