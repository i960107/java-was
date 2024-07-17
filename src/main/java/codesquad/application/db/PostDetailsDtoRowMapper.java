package codesquad.application.db;

import codesquad.application.model.PostDetailsDto;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostDetailsDtoRowMapper implements RowMapper<PostDetailsDto> {

    @Override
    public PostDetailsDto mapRow(ResultSet rs) throws SQLException {
        return new PostDetailsDto(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getLong("userId"),
                rs.getString("nickname"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
