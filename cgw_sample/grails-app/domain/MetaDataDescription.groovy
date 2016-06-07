package edu.wustl.cgw.domain

import org.hibernate.type.LocaleType


class MetaDataDescription {

    Locale locale
    String value


    static mapping = {
        locale type: LocaleType
        id generator:'sequence', params:[sequence:'meta_data_description_id_seq']

    }


}
