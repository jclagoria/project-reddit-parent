package ar.com.reddit.service;

import ar.com.reddit.dto.VoteDto;
import ar.com.reddit.exceptions.PostNotFoundException;
import ar.com.reddit.exceptions.SpringRedditException;
import ar.com.reddit.model.Post;
import ar.com.reddit.model.Vote;
import ar.com.reddit.respository.PostRepository;
import ar.com.reddit.respository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static ar.com.reddit.model.VoteType.UPVOTE;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto
                .getPostId()).orElseThrow(() -> new
                PostNotFoundException("Post Not Found with ID - "
                + voteDto.getPostId()));

        Optional<Vote> voteByPostAndUser = voteRepository
                .findTopByPostAndUserOrderByVoteIdDesc(post,
                        authService.getCurrentUser());

        if(voteByPostAndUser.isPresent()
                && voteByPostAndUser.get().getVoteType()
                .equals(voteDto.getVoteType())){
            throw new SpringRedditException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }

        if(UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }

}
