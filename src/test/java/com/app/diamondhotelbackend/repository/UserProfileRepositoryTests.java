package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.util.Constant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class UserProfileRepositoryTests {

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UserProfile userProfile;

    private List<UserProfile> userProfileList;

    @BeforeEach
    public void init() {
        userProfile = UserProfile.builder()
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        userProfileList = List.of(
                UserProfile.builder()
                        .email("ala-gembala@wp.pl")
                        .passportNumber("ZF005401499")
                        .role(Constant.USER)
                        .authProvider(Constant.LOCAL)
                        .accountConfirmed(false)
                        .build(),
                UserProfile.builder()
                        .email("beata-pacanek@wp.pl")
                        .passportNumber("DF115499499")
                        .role(Constant.USER)
                        .authProvider(Constant.LOCAL)
                        .accountConfirmed(false)
                        .build()
        );
    }

    @Test
    public void UserProfileRepository_SaveAll_ReturnSavedUserProfile() {
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isGreaterThan(0);
    }

    @Test
    public void UserProfileRepository_FindAll_ReturnMoreThenOneUserProfile() {
        userProfileRepository.saveAll(userProfileList);
        List<UserProfile> foundUserProfileList = userProfileRepository.findAll();

        Assertions.assertThat(foundUserProfileList).isNotNull();
        Assertions.assertThat(foundUserProfileList.size()).isEqualTo(2);
    }

    @Test
    public void UserProfileRepository_FindById_ReturnUserProfile() {
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById((savedUserProfile.getId()));

        Assertions.assertThat(userProfileOptional).isPresent();
        Assertions.assertThat(userProfileOptional.get().getId()).isEqualTo(savedUserProfile.getId());
    }

    @Test
    public void UserProfileRepository_FindByEmail_ReturnUserProfile() {
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByEmail((savedUserProfile.getEmail()));

        Assertions.assertThat(userProfileOptional).isPresent();
        Assertions.assertThat(userProfileOptional.get().getId()).isEqualTo(savedUserProfile.getId());
    }

    @Test
    public void UserProfileRepository_Update_ReturnUserProfileNotNull() {
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById((savedUserProfile.getId()));

        Assertions.assertThat(userProfileOptional).isPresent();
        Assertions.assertThat(userProfileOptional.get().getId()).isEqualTo(savedUserProfile.getId());

        userProfileOptional.get().setEmail("magda-lampa@gmail.com");
        userProfileOptional.get().setRole("ADMIN");
        UserProfile updatedUserProfile = userProfileRepository.save(userProfileOptional.get());

        Assertions.assertThat(updatedUserProfile.getEmail()).isEqualTo("magda-lampa@gmail.com");
        Assertions.assertThat(updatedUserProfile.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void UserProfileRepository_Delete_ReturnUserProfileIsEmpty() {
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        userProfileRepository.deleteById(savedUserProfile.getId());
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(userProfile.getId());

        Assertions.assertThat(userProfileOptional).isEmpty();
    }
}
