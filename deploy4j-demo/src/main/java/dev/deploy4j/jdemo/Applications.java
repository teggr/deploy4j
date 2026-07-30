package dev.deploy4j.jdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class Applications {

  private final JdbcTemplate jdbcTemplate;

  public record ApplicationRecord(Long id, String name) {}

  public java.util.List<ApplicationRecord> listApplications() {
    return jdbcTemplate.query(
      "SELECT id, name FROM application",
      (rs, rowNum) -> new ApplicationRecord(rs.getLong("id"), rs.getString("name"))
    );
  }

  @Transactional
  public void addApplication(String name) {
    jdbcTemplate.update("INSERT INTO application (name) VALUES (?)", name);
  }

  @Transactional
  public void updateApplication(Long id, String name) {
    jdbcTemplate.update("UPDATE application SET name = ? WHERE id = ?", name, id);
  }

  @Transactional
  public void deleteApplication(Long id) {
    jdbcTemplate.update("DELETE FROM application WHERE id = ?", id);
  }

}
