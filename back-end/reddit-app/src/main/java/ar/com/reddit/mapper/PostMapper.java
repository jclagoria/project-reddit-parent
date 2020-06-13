package ar.com.reddit.mapper;

import ar.com.reddit.dto.PostRequest;
import ar.com.reddit.dto.PostResponse;
import ar.com.reddit.model.Post;
import ar.com.reddit.model.Subreddit;
import ar.com.reddit.model.User;
import ar.com.reddit.respository.CommentRepository;
import ar.com.reddit.respository.VoteRepository;
import ar.com.reddit.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import javafx.geometry.Pos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.soap.SOAPBinding;
import java.sql.Time;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;

    @Mapping(target = "createDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "voteCount", constant = "0")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse mapToDto(Post post);

    Integer commentCount(Post post) {
        return commentRepository
                .findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo
                .using(post.getCreateDate().toEpochMilli());
    }

}
