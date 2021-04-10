from rest_framework import permissions
from rest_framework.decorators import api_view
from rest_framework.generics import ListCreateAPIView, RetrieveUpdateDestroyAPIView, RetrieveAPIView
from rest_framework.response import Response
from userApi.u_serializers import UserSerializer, MyPageUserSerializer \
    , SearchAccountSerializer, FollowingSerializer, FollowerSerializer
from userApi.models import User, FollowingRelation
import json
from django.db.models import Count
from google.oauth2 import id_token
from google.auth.transport import requests
from .utils import create_user_as_google, check_nickname
from django.utils import timezone
from recipeApi.models import History


@api_view(['post'])
def push_follow(request, user_id):
    print(request.GET)
    print(user_id)
    pushed_person = request.GET['from']
    following_user = User.objects.get(id=user_id)
    follower_user = User.objects.get(id=pushed_person)
    try:
        item = FollowingRelation.objects.get(follower=follower_user, following=following_user)
        item.delete()
        try:
            hist = History.objects.get(user=following_user, model_id=follower_user.id, classify="UF")
            hist.delete()
        except:
            pass
        return Response(False)
    except Exception as e:
        FollowingRelation.objects.create(follower=follower_user, following=following_user)
        History.objects.create(user=following_user, model_id=follower_user.id, classify="UF")
        return Response(True)


@api_view(['get'])
def get_follower_list(request, user_id):
    user = User.objects.get(id=user_id)
    relation = FollowingRelation.objects.filter(following=user)
    ser = FollowerSerializer(relation, many=True)
    return Response(ser.data)


@api_view(['get'])
def get_following_list(request, user_id):
    user = User.objects.get(id=user_id)
    relation = FollowingRelation.objects.filter(follower=user)
    ser = FollowingSerializer(relation, many=True)
    return Response(ser.data)


@api_view(['get'])
def get_following_weeks_list(request, user_id):
    user = User.objects.get(id=user_id)
    right_now = timezone.localtime()
    week_ago = right_now - timezone.timedelta(days=7)
    print(right_now)
    print(week_ago)
    relation = FollowingRelation.objects.filter(follower=user, created_time__gte=week_ago)
    ser = FollowingSerializer(relation, many=True)
    return Response(ser.data)


@api_view(['get'])
def get_user_page(request, pk):
    from_pk = request.GET['from']
    print(pk)
    print(from_pk)
    user = User.objects.get(pk=pk)
    user_ser = MyPageUserSerializer(user).data
    print(user_ser)
    if pk == from_pk:
        user_ser['is_follow'] = True
        return Response(user_ser)
    try:
        follow_user = User.objects.get(pk=from_pk)
        user.relation_following.get(follower=follow_user)
        user_ser['is_follow'] = True
        return Response(user_ser)
    except Exception as e:
        user_ser['is_follow'] = False
        print(e)
    finally:
        return Response(user_ser)


@api_view(['post'])
def post_id_token(request):
    client_id = ''

    # tok = request.GET.get('id_token')
    received_json_data = json.loads(request.body)
    tok = received_json_data['idToken']
    # print(tok)
    try:
        id_info = id_token.verify_oauth2_token(tok, requests.Request(), client_id)
        if id_info['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
            print("err in id_info")
            raise ValueError('Wrong issuer.')
    except Exception as e:
        print(e)
        pass
        # print(">>> trace back <<<")
        # traceback.print_exc()
        # print(">>> trace back <<<")
    user = create_user_as_google(id_info)
    serializers = UserSerializer(user)
    return Response(serializers.data)


class UsersList(ListCreateAPIView):
    # queryset= User.objects.~~
    queryset = User.objects.all()[:5]
    permission_classes = [
        permissions.AllowAny
    ]
    serializer_class = UserSerializer

    def get(self, request, *args, **kwargs):
        print("\nrequest : ", request.body)
        return super().get(request, *args, **kwargs)

    def post(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().post(request, *args, **kwargs)


class UserDetailAPIView(RetrieveUpdateDestroyAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    lookup_field = 'user_id'  ## 기본값 : pk

    def get(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().get(request, *args, **kwargs)

    def patch(self, request, *args, **kwargs):
        req_data = request.data
        try:
            image = req_data['profile']
            setting = json.loads(req_data['user'])
            user = User.objects.get(id=int(setting['id']))
            user.profile_image = image
            user.favorite_food = setting['favorite_food']
            try:
                user.introduce = setting['introduce']
            except:
                pass
            try:
                user.nickname = setting['nickname']
            except:
                pass
        except:
            setting = json.loads(req_data['user'])
            user = User.objects.get(user_id=kwargs['user_id'])
            user.favorite_food = setting['favorite_food']
            user.nickname = setting['nickname']
            user.introduce = setting['introduce']
            user.save()
            pass


        user.save()
        return super().patch(request, *args, **kwargs)

    def put(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().put(request, *args, **kwargs)


class SearchUserList(ListCreateAPIView):
    serializer_class = SearchAccountSerializer

    def get_queryset(self):
        get_query_set = self.request.GET
        search_text = get_query_set['query']
        if search_text != "":
            user = User.objects.prefetch_related('followers').filter(nickname__icontains=search_text).annotate(
                followers_count=Count("relation_follower")
            ).order_by('-followers_count')
            for u in user:
                print(u.nickname)
                print(u.followers.all())
        else:
            user = None
        return user


@api_view(['GET'])
def get_check_nickname(request):
    return Response(check_nickname(request.GET.get('nickname')))
# @api_view(['GET'])
# def users(request):
#     userApi = User.objects.order_by('-join_date')
#     serializers = UserSerializer(userApi, many=True)
#     return Response(serializers.data)
#

#
#
# def results(request, user_id):
#     response = "You are looking at the result of userid %s."
#     return HttpResponse(response % user_id)
#
#
# def user_image(request, user_id):
#     path = '%s\'s Image Path : ' % user_id
#     return HttpResponse(path)
#
#
# def detail(request, id):
#     userApi = get_object_or_404(User, user_id=id)  # shortcuts
#     # try:
#     #     userApi = User.objects.get(pk=user_id)
#     # except User.DoesNotExist:
#     #     raise Http404("Question does not exist")
#     profile_image = get_object_or_404(UserProfileImage, user_id=userApi.pk)
#     full = "userApi : %s, image : %s" % (userApi, profile_image)
#     # return HttpResponse(full)
#     return HttpResponseRedirect(reverse('results', args=(profile_image.pk,)))
