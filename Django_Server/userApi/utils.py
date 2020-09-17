from .models import User
import base64


def create_user_as_google(id_info):
    user_id = id_info['email']
    try:
        user = User.objects.get(user_id=user_id)
    except Exception as e:
        print(e)
        user = User(user_id=user_id, name=id_info['name'], member_type=1)
        user.save()
    return user


def image_to_string_u(image):
    if image.name is None:
        return None
    return base64.b64encode(image.read())


def check_nickname(nickname):
    print(nickname)
    try:
        user = User.objects.get(nickname=nickname)
        print(user)
        return False
    except Exception as e:
        print(e)
        return True
