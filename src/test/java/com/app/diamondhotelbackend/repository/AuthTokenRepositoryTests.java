package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class AuthTokenRepositoryTests {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private UserProfile savedUserProfile;

    private AuthToken authToken;

    private List<AuthToken> authTokenList;

    @BeforeEach
    public void init() {
        UserProfile userProfile = UserProfile.builder()
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .build();

        savedUserProfile = testEntityManager.persistAndFlush(userProfile);

        authToken = AuthToken.builder()
                .userProfile(savedUserProfile)
                .accessValue("accessValue")
                .refreshValue("refreshValue")
                .build();

        authTokenList = List.of(
                AuthToken.builder()
                        .userProfile(savedUserProfile)
                        .accessValue("accessValue1")
                        .refreshValue("refreshValue1")
                        .build(),
                AuthToken.builder()
                        .userProfile(savedUserProfile)
                        .accessValue("accessValue2")
                        .refreshValue("refreshValue2")
                        .build()
        );
    }

    @Test
    public void AuthTokenRepository_Save_ReturnsAuthToken() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);

        Assertions.assertThat(savedAuthToken).isNotNull();
        Assertions.assertThat(savedAuthToken.getId()).isGreaterThan(0);
    }

    @Test
    public void AuthTokenRepository_FindAll_ReturnsAuthTokenList() {
        authTokenRepository.saveAll(authTokenList);
        List<AuthToken> foundAuthTokenList = authTokenRepository.findAll();

        Assertions.assertThat(foundAuthTokenList).isNotNull();
        Assertions.assertThat(foundAuthTokenList.size()).isEqualTo(2);
    }

    @Test
    public void AuthTokenRepository_FindById_ReturnsOptionalAuthToken() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);
        Optional<AuthToken> authTokenOptional = authTokenRepository.findById((savedAuthToken.getId()));

        Assertions.assertThat(authTokenOptional).isPresent();
        Assertions.assertThat(authTokenOptional.get().getId()).isEqualTo(savedAuthToken.getId());
    }

    @Test
    public void AuthTokenRepository_FindByAccessValue_ReturnsOptionalAuthToken() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);
        Optional<AuthToken> authTokenOptional = authTokenRepository.findByAccessValue(savedAuthToken.getAccessValue());

        Assertions.assertThat(authTokenOptional).isPresent();
        Assertions.assertThat(authTokenOptional.get().getAccessValue()).isEqualTo(savedAuthToken.getAccessValue());
    }

    @Test
    public void AuthTokenRepository_FindByRefreshValue_ReturnsOptionalAuthToken() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);
        Optional<AuthToken> authTokenOptional = authTokenRepository.findByRefreshValue((savedAuthToken.getRefreshValue()));

        Assertions.assertThat(authTokenOptional).isPresent();
        Assertions.assertThat(authTokenOptional.get().getRefreshValue()).isEqualTo(savedAuthToken.getRefreshValue());
    }

    @Test
    public void AuthTokenRepository_FindByUserProfile_ReturnsOptionalAuthToken() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);
        Optional<AuthToken> optionalAuthToken = authTokenRepository.findByUserProfile(savedUserProfile);

        Assertions.assertThat(optionalAuthToken).isPresent();
        Assertions.assertThat(optionalAuthToken.get().getId()).isEqualTo(savedAuthToken.getId());
        Assertions.assertThat(optionalAuthToken.get().getUserProfile()).isEqualTo(savedUserProfile);
    }

    @Test
    public void AuthTokenRepository_Update_ReturnsAuthToken() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);
        Optional<AuthToken> authTokenOptional = authTokenRepository.findById((savedAuthToken.getId()));

        Assertions.assertThat(authTokenOptional).isPresent();
        Assertions.assertThat(authTokenOptional.get().getId()).isEqualTo(savedAuthToken.getId());

        authTokenOptional.get().setAccessValue("updatedAccessValue");
        authTokenOptional.get().setRefreshValue("updatedRefreshValue");
        AuthToken updatedAuthToken = authTokenRepository.save(authTokenOptional.get());

        Assertions.assertThat(updatedAuthToken).isNotNull();
        Assertions.assertThat(updatedAuthToken.getAccessValue()).isEqualTo("updatedAccessValue");
        Assertions.assertThat(updatedAuthToken.getRefreshValue()).isEqualTo("updatedRefreshValue");
    }

    @Test
    public void AuthTokenRepository_Delete_ReturnsNothing() {
        AuthToken savedAuthToken = authTokenRepository.save(authToken);
        authTokenRepository.deleteById(savedAuthToken.getId());
        Optional<AuthToken> authTokenOptional = authTokenRepository.findById(savedAuthToken.getId());

        Assertions.assertThat(authTokenOptional).isEmpty();
    }
}
