package edu.wustl.cgw.domain


/**
 * @author Sameer Sawant
 * @since CGW-2.2
 */
class Institution implements Serializable{


    static mapping = {
        id generator:'sequence', params:[sequence:'institution_id_seq']
    }

}
