package codesquad.application.db;

import codesquad.application.model.Post;
import codesquad.application.model.PostDetailsDto;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostDao {

    private static final String INSERT_SQL = "INSERT INTO `post`(user_id, title, content, filename, created_at) VALUES(?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL = "SELECT * FROM `post` where id = ?";

    private static final String FIND_ALL_SQL = "SELECT * FROM `post`";

    private static final String DELETE_ALL_SQL = "DELETE FROM `post`";

    private static final String FIND_ALL_POST_WITH_DETAIL_SQL =
            "SELECT p.id, title, content, u.id as userId, nickname, filename, p.created_at "
                    + "FROM `post` p JOIN `user` u ON p.user_id = u.id ORDER BY p.created_at DESC";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    private final PostRowMapper postRowMapper;

    private final PostDetailsDtoRowMapper postDetailsDtoRowMapper;

    public PostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.postRowMapper = new PostRowMapper();
        this.postDetailsDtoRowMapper = new PostDetailsDtoRowMapper();
    }


    public Post save(Post post) {
        if (post == null) {
            log.warn("post cannot be null" );
            throw new IllegalArgumentException("post is null");
        }
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        Long generatedId = jdbcTemplate.saveAndGetGeneratedKey(INSERT_SQL, pstmt -> {
            try {
                pstmt.setLong(1, post.getUserId());
                pstmt.setString(2, post.getTitle());
                pstmt.setString(3, post.getContent());
                pstmt.setString(4, post.getFileName());
                pstmt.setTimestamp(5, Timestamp.valueOf(post.getCreatedAt()));
            } catch (SQLException e) {
                log.warn("error while prepare statement : {}" , INSERT_SQL);
                throw new DBException("error while prepare statement " + INSERT_SQL);
            }
        });
        post.setId(generatedId);
        return post;
    }

    public Optional<Post> findById(Long id) {
        Post post = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, pstmt -> {
                    try {
                        pstmt.setLong(1, id);
                    } catch (SQLException e) {
                        log.warn("error while prepare statement : {}" , FIND_BY_ID_SQL);
                        throw new DBException("error while prepare statement " + FIND_BY_ID_SQL);
                    }
                },
                postRowMapper);
        return Optional.ofNullable(post);
    }


    public List<Post> findAll() {
        return jdbcTemplate.queryForList(FIND_ALL_SQL, pstmt -> {
        }, postRowMapper);
    }

    public int deleteAll() {
        return jdbcTemplate.executeUpdate(DELETE_ALL_SQL, preparedStatement -> {
        });
    }

    public List<PostDetailsDto> findAllPostWithDetail() {
        return jdbcTemplate.queryForList(FIND_ALL_POST_WITH_DETAIL_SQL, pstmt -> {
        }, postDetailsDtoRowMapper);
    }
}
