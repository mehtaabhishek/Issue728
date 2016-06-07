package edu.wustl.cgw.domain

import org.hibernate.type.LocaleType

class MetaDataLabel {

    Locale locale
    String value

    static mapping = {
        locale type: LocaleType
        id generator: 'sequence', params: [sequence: 'meta_data_label_id_seq']

    }


}
