
def get_string_from_file(full_file_name):
    with open(full_file_name) as file_obj:
        result_string = file_obj.read()
    return result_string