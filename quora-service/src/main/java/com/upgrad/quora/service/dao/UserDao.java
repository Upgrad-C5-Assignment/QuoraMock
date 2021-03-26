package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

/**
 * Dao class which deals with database logic related to User entity
 *
 * @author saif
 * @author sanatt
 */
@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        try {
            entityManager.persist(userEntity);
            return userEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public UserEntity getUserByUsername(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", userName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    public UserEntity getUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserById(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createAuth(final UserAuthEntity userAuthEntity) {
        try {
            entityManager.persist(userAuthEntity);
            return userAuthEntity;
        }
        catch (Exception e) {
            return null;
        }
    }

    public UserAuthEntity getUserAuthEntityByToken(final String accessToken) {
        try {
            UserAuthEntity authEntity = entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();

            return authEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public int updateUserLogoutByToken(final String accessToken, final ZonedDateTime logoutAt) {
        try {
            int updateStatus = entityManager.createNamedQuery("userLogoutByAccessToken")
                    .setParameter("accessToken", accessToken)
                    .setParameter("logoutAt", logoutAt)
                    .executeUpdate();

            return updateStatus;

        } catch (NoResultException nre) {
            return -1;
        }
    }

    public UserEntity deleteUser(final String userId) {
        UserEntity deleteUser = getUserById(userId);
        if (deleteUser != null) {
            this.entityManager.remove(deleteUser);
        }
        return deleteUser;
    }

}

