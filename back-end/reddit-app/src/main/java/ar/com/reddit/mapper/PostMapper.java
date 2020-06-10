package ar.com.reddit.mapper;

import ar.com.reddit.respository.CommentRepository;
import ar.com.reddit.respository.VoteRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VoteRepository voteRepository;


}
