package org.example.crm.repositories.specifications

import org.example.crm.entities.Contact
import org.example.crm.entities.Email
import org.example.crm.entities.Telephone
import org.example.crm.utils.enums.Category
import org.springframework.data.jpa.domain.Specification

object ContactSpecs {
    fun nameStartWithIgnoreCase(name: String): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "${name.lowercase()}%"
            )
        }
    }

    fun surnameStartWithIgnoreCase(surname: String): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("surname")),
                "${surname.lowercase()}%"
            )
        }
    }

    fun ssnCodeStartWithIgnoreCase(ssnCode: String): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("ssnCode")),
                "${ssnCode.lowercase()}%"
            )
        }
    }

    fun categoryEqual(category: Category): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.get<Category>("category"),
                category
            )
        }
    }

    fun telephoneEqual(telephone: String): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.joinList<Telephone, String>("telephones").get<String>("telephone"),
                telephone
            )
        }
    }

    fun emailEqual(email: String): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.joinList<Email, String>("emails").get<String>("email"),
                email
            )
        }
    }

    fun locationStartWithIgnoreCase(location: String): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get<String?>("professional").get("location")),
                "${location.lowercase()}%"
            )
        }
    }

    fun dailyRateGraterThanEqual(dailyRate: Float): Specification<Contact> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(
                root.get<Float>("professional").get("dailyRate"),
                dailyRate
            )
        }
    }

    //TODO: add skills spec
}