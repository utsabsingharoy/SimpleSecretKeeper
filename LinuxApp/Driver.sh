#! /bin/bash

function show_encryption_error () {
    zenity --error --text "encryption failed"
}

function show_decryption_error () {
    zenity --error --text "decryption failed"
}

choice=`zenity --list --radiolist --column="check" --column="Options" 1 "new"  2 "edit"  3 "show" 4 "change password"`
case $choice in
    new)
        plain_text=`zenity --text-info --editable`
        filename=`zenity --entry --text="file name"`
        password=`zenity --entry --text="password" --hide-text`
        if [ ! -z $plain_text ]
        then
            if [ ! -z $filename ]
            then
                if [ ! -z $password ]
                then
                    ./a.out encrypt $plain_text $filename $password
                    if [ $? != 0 ]
                    then
                        show_encryption_error
                    fi
                fi
            fi
        fi
        ;;
    edit)
        filename=`zenity --file-selection`
        password=`zenity --password`
        dectypted_text=`./a.out decrypt $filename $password`
        if [ $? != 0 ]
        then
            show_decryption_error
        else
            edited_text=`echo $dectypted_text | zenity --text-info --editable`
            if [ dectypted_text != edited_text ]
            then
                mv $filename $filename+"old" 
                ./a.out encrypt $edited_text $filename $password
                if [ $? != 0 ]
                then
                    mv $filename+"old" $filename
                    show_encryption_error
                else
                    rm -rf $filename+"old"
                fi
            fi
        fi
        ;;
    show)
        filename=`zenity --file-selection`
        password=`zenity --password`
        dectypted_text=`./a.out decrypt $filename $password`
        if [ $? == 0 ]
        then
            echo $dectypted_text | zenity --text-info 
        else
            show_decryption_error
        fi
        ;;
    "change password")
        filename=`zenity --file-selection`
        password=`zenity --password`
        dectypted_text=`./a.out decrypt $filename $password`
        if [ $? != 0 ]
        then
            show_decryption_error
        else
            new_password=`zenity --entry --text="new password" --hide-text`
            if [ ! -z $new_password ]
            then
                password=$new_password
            fi

            mv $filename $filename+"old" 
            ./a.out encrypt $dectypted_text $filename $password
            if [ $? != 0 ]
            then
                mv $filename+"old" $filename
                show_encryption_error
            else
                rm -rf $filename+"old"
            fi
        fi
        ;;
    *)
        echo "none"
        ;;
esac
