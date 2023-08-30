package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class ConfirmationTokenRepositoryTests {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private ConfirmationToken confirmationToken;

    private List<ConfirmationToken> confirmationTokenList;

    @BeforeEach
    public void init() {
        long expiration = 1000 * 60 * 15;

        UserProfile userProfile = UserProfile.builder()
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .build();

        UserProfile savedUserProfile = testEntityManager.persistAndFlush(userProfile);

        confirmationToken = ConfirmationToken.builder()
                .userProfile(savedUserProfile)
                .accessValue("accessValue")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + expiration))
                .build();

        confirmationTokenList = List.of(
                ConfirmationToken.builder()
                        .userProfile(savedUserProfile)
                        .accessValue("accessValue1")
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + expiration))
                        .build(),
                ConfirmationToken.builder()
                        .userProfile(savedUserProfile)
                        .accessValue("accessValue2")
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + expiration))
                        .build()
        );
    }

    @Test
    public void ConfirmationTokenRepository_Save_ReturnsConfirmationToken() {
        ConfirmationToken savedConfirmationToken = confirmationTokenRepository.save(confirmationToken);

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getId()).isGreaterThan(0);
    }

    @Test
    public void ConfirmationTokenRepository_FindAll_ReturnsConfirmationTokenList() {
        confirmationTokenRepository.saveAll(confirmationTokenList);
        List<ConfirmationToken> foundConfirmationTokenList = confirmationTokenRepository.findAll();

        Assertions.assertThat(foundConfirmationTokenList).isNotNull();
        Assertions.assertThat(foundConfirmationTokenList.size()).isEqualTo(2);
    }

    @Test
    public void ConfirmationTokenRepository_FindById_ReturnsOptionalConfirmationToken() {
        ConfirmationToken savedConfirmationToken = confirmationTokenRepository.save(confirmationToken);
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findById((savedConfirmationToken.getId()));

        Assertions.assertThat(confirmationTokenOptional).isPresent();
        Assertions.assertThat(confirmationTokenOptional.get().getId()).isEqualTo(savedConfirmationToken.getId());
    }

    @Test
    public void ConfirmationTokenRepository_FindByAccessValue_ReturnsOptionalConfirmationToken() {
        ConfirmationToken savedConfirmationToken = confirmationTokenRepository.save(confirmationToken);
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findByAccessValue((savedConfirmationToken.getAccessValue()));

        Assertions.assertThat(confirmationTokenOptional).isPresent();
        Assertions.assertThat(confirmationTokenOptional.get().getAccessValue()).isEqualTo(savedConfirmationToken.getAccessValue());
    }

    @Test
    public void ConfirmationTokenRepository_Update_ReturnsConfirmationToken() {
        ConfirmationToken savedConfirmationToken = confirmationTokenRepository.save(confirmationToken);
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findById((savedConfirmationToken.getId()));

        Assertions.assertThat(confirmationTokenOptional).isPresent();
        Assertions.assertThat(confirmationTokenOptional.get().getId()).isEqualTo(savedConfirmationToken.getId());

        confirmationTokenOptional.get().setAccessValue("updatedAccessValue");
        confirmationTokenOptional.get().setConfirmedAt(new Date(System.currentTimeMillis()));
        ConfirmationToken updatedConfirmationToken = confirmationTokenRepository.save(confirmationTokenOptional.get());

        Assertions.assertThat(updatedConfirmationToken.getAccessValue()).isEqualTo("updatedAccessValue");
        Assertions.assertThat(updatedConfirmationToken.getConfirmedAt()).isNotNull();
    }

    @Test
    public void ConfirmationTokenRepository_Delete_ReturnsNothing() {
        ConfirmationToken savedConfirmationToken = confirmationTokenRepository.save(confirmationToken);
        confirmationTokenRepository.deleteById(savedConfirmationToken.getId());
        Optional<ConfirmationToken> ConfirmationTokenOptional = confirmationTokenRepository.findById(savedConfirmationToken.getId());

        Assertions.assertThat(ConfirmationTokenOptional).isEmpty();
    }
}
