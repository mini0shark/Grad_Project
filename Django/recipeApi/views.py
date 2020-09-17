from django.http import HttpResponse
from django.utils import timezone
from rest_framework import permissions
from rest_framework.decorators import api_view
from rest_framework.generics import RetrieveUpdateDestroyAPIView, \
    ListCreateAPIView
from rest_framework.response import Response
from django.db.models import Q
from recipeApi.models import History
from recipeApi.models import RecipeContainer, Comment, RecipeWishListRelation, CommentLikeRelation
from recipeApi.r_serializers import CommentSerializer, \
    SimpleRecipeContainerSerializer, HomePageSerializer, RecipeDetailSerializer, \
    HistorySerializer, MyPageRecipeContainerSerializer, SearchReferenceSerializer, \
    StoryDetailSerializer, SearchStorySerializer, CopiedItemsSerializer, WishListSerializer
from recipeApi.utils import post_register_result, post_register_story, get_single_recipe \
    , like_dislike, edit_register_result, edit_story_result
from userApi.models import User


def index(request):
    return HttpResponse("Hello, world. You're at the polls index.")


@api_view(['post'])
def register_recipe_container(request, user_id):
    if request.method == 'POST':
        return Response(post_register_result(request, user_id))
    return Response(False)


@api_view(['post'])
def edit_recipe_container(request, recipe_id):
    if request.method == 'POST':
        recipe = RecipeContainer.objects.get(id=recipe_id)
        user_id = recipe.user.id
        if recipe.type:
            return Response(edit_register_result(request, recipe_id, user_id))
        else:
            return Response(edit_story_result(request, recipe_id, user_id))
    return Response(False)


@api_view(['post'])
def register_story(request, user_id):
    if request.method == 'POST':
        return Response(post_register_story(request, user_id))
    return Response(False)


@api_view(['post'])
def push_like(request, recipe_id, user_id):
    if request.method == 'POST':
        user = User.objects.get(pk=user_id)
        recipe = RecipeContainer.objects.get(id=recipe_id)
        flag = request.data
        likes = recipe.likes.all()
        print(likes)
        return Response(like_dislike(flag, recipe, user))
    return Response(False)


@api_view(['post'])
def push_comment_like(request, comment_id, user_id):
    if request.method == 'POST':
        user = User.objects.get(pk=user_id)
        comment = Comment.objects.get(id=comment_id)
        try:
            comment_like = CommentLikeRelation.objects.get(to_comment=comment, from_user=user)
            comment_like.delete()
            try:
                hist = History.objects.get(classify='CL', model_id=comment.id)
                if comment.comment_like_comment.count == 1:
                    hist.delete()
            except:
                print("didn't")
            print("result : ", False)
            return Response(False)
        except Exception as e:
            CommentLikeRelation.objects.create(to_comment=comment, from_user=user)
            print(e)
            try:
                hist = History.objects.get(classify='CL', model_id=comment.id)
                hist.update_time = timezone.now()
                hist.save()
            except Exception as e:
                print(e)
                try:
                    History.objects.create(classify='CL', model_id=comment.id, user=comment.user)
                except Exception as e:
                    print(e)
            print("result : ", True)
            return Response(True)


@api_view(['get'])
def get_user_history(request, user_id):
    order = int(request.GET['order'])
    print("user : ", user_id, " order  ", order)
    user = User.objects.get(id=user_id)
    # 본인거 + follow한 유저거
    start = order * 7
    end = start + 7
    print("st : ", start, "end : ", end)
    try:
        history = History.objects.filter(user=user).order_by('-update_time')[start:end]
        return Response(HistorySerializer(history, many=True).data)
    except Exception as e:
        print(e)
        return Response(None)


@api_view(['get'])
def get_my_page_item(request, user_id, kind):
    if kind == "BookMark":
        user = User.objects.get(id=user_id)
        obj = user.recipe_wish_list_user.all()
        ser = WishListSerializer(obj, many=True).data
        return Response(ser)
    elif kind == "Recipe":
        sort = True
    elif kind == "Story":
        sort = False
    obj = RecipeContainer.objects.filter(user_id=user_id, type=sort).order_by('-created_date')
    ser = MyPageRecipeContainerSerializer(obj, many=True).data
    return Response(ser)


class ContainerDetailAPIView(RetrieveUpdateDestroyAPIView):
    permission_classes = [
        permissions.AllowAny
    ]
    lookup_field = "id"
    serializer_class = RecipeDetailSerializer

    def get_object(self):
        recipe_id = self.kwargs['id']
        return RecipeContainer.objects.get(id=recipe_id)

    def delete(self, request, *args, **kwargs):
        print(self.kwargs)
        try:
            recipe_id = self.kwargs['id']
            histories = History.objects.filter(Q(classify='RR', model_id=recipe_id)
                                               | Q(classify='RL', model_id=recipe_id)
                                               | Q(classify='RC', model_id=recipe_id))
            for his in histories:
                his.delete()
        except:
            pass
        return super().delete(request, *args, **kwargs)


class HomeContainerList(ListCreateAPIView):
    # queryset = RecipeContainer.objects.filter(user_id=2)
    permission_classes = [
        permissions.AllowAny
    ]
    serializer_class = HomePageSerializer

    # 여기에서 유저별로 다른 홈 아이템들을 뿌릴 수 있게
    def get_queryset(self):
        user_id = self.kwargs['user_id']
        order = int(self.request.GET['order'])
        user = User.objects.get(user_id=user_id)
        followings = user.relation_follower.all()
        print("Followers : ", followings, "\n")
        home_list = user.recipe_container.all()
        for usr in followings:
            add_lst = usr.following.recipe_container.all()
            print("follower : ", usr.follower)
            print("following : ", usr.following)
            print("addList : ", add_lst)
            if home_list.count() < 1:
                home_list = add_lst
            else:
                home_list = home_list | add_lst
        # 본인거 + follow한 유저거
        start = order * 5
        end = start + 5
        try:
            recipe_container_object = home_list.order_by('-created_date')[start:end]
        except Exception as e:
            print(e)
        finally:
            return recipe_container_object

    def get(self, request, *args, **kwargs):
        print("\nrequest : ", request.body)
        return super().get(request, *args, **kwargs)

    def post(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().post(request, *args, **kwargs)


class CopiedStoryList(ListCreateAPIView):
    # queryset = RecipeContainer.objects.filter(user_id=2)
    permission_classes = [
        permissions.AllowAny
    ]
    serializer_class = CopiedItemsSerializer

    # 여기에서 유저별로 다른 홈 아이템들을 뿌릴 수 있게
    def get_queryset(self):
        recipe = RecipeContainer.objects.get(id=self.kwargs['recipe_id'])
        copied_list = recipe.recipe_original.all()
        print(copied_list)
        result = []
        for o in copied_list:
            result.append(o.recipe_copied)
        print(result)
        recipe_container_object = RecipeContainer.objects.all().order_by('-id')[:7]
        return result

    def get(self, request, *args, **kwargs):
        print("\nrequest : ", request.body)
        return super().get(request, *args, **kwargs)


class RecipeDetailAPIView(RetrieveUpdateDestroyAPIView):
    serializer_class = RecipeDetailSerializer

    def get_object(self):
        container = RecipeContainer.objects.get(id=self.kwargs['recipe_id'])
        container.hit_count += 1
        container.save()
        return get_single_recipe(self.kwargs['recipe_id'])

    def get(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().get(request, *args, **kwargs)

    def patch(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().patch(request, *args, **kwargs)

    def put(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().put(request, *args, **kwargs)


class StoryDetailAPIView(RetrieveUpdateDestroyAPIView):
    serializer_class = StoryDetailSerializer

    def get_object(self):
        container = RecipeContainer.objects.get(id=self.kwargs['recipe_id'])
        container.hit_count += 1
        container.save()
        return get_single_recipe(self.kwargs['recipe_id'])

    def get(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().get(request, *args, **kwargs)

    def patch(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().patch(request, *args, **kwargs)

    def put(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().put(request, *args, **kwargs)


class WishListList(ListCreateAPIView):
    serializer_class = WishListSerializer
    permission_classes = [
        permissions.AllowAny
    ]

    def get_queryset(self):
        # data1 = self.request.data['user_id']
        user = self.request.GET['user_id']
        wish_list = RecipeWishListRelation.objects.filter(user=user)
        return wish_list

    def get(self, request, *args, **kwargs):
        print("\nrequest : ", request.body)
        return super().get(request, *args, **kwargs)

    def post(self, request, *args, **kwargs):
        user_id = request.data['user_id']
        recipe_id = request.data['recipe_id']
        recipe = RecipeContainer.objects.get(id=recipe_id)
        user = User.objects.get(id=user_id)
        try:
            item = RecipeWishListRelation.objects.get(recipe=recipe, user=user)
            item.delete()
            return Response(False)
        except Exception as e:
            print(e)
            RecipeWishListRelation.objects.create(recipe=recipe, user=user)
            return Response(True)


class CommentList(ListCreateAPIView):
    # queryset = RecipeContainer.objects.filter(user_id=2)
    serializer_class = CommentSerializer
    permission_classes = [
        permissions.AllowAny
    ]

    def get_queryset(self):
        comment = Comment.objects.all()[:5]
        return comment

    def get(self, request, *args, **kwargs):
        print("\nrequest : ", request.body)
        return super().get(request, *args, **kwargs)

    def post(self, request, *args, **kwargs):
        print("----------------")
        print(request.data)
        user_id = request.data['user_id']
        recipe_container_id = request.data['recipe_container_id']
        text = request.data['text']
        # tag 나중에 하기
        comment = Comment(text=text)
        comment.recipe_container = RecipeContainer.objects.get(id=recipe_container_id)
        comment.user = User.objects.get(id=user_id)
        comment.save()
        data = CommentSerializer(comment).data
        print("----------------")
        try:
            recipe = RecipeContainer.objects.get(id=recipe_container_id)
            History.objects.create(classify='RC', model_id=recipe.id, user=recipe.user)
            print("create")
        except Exception as e:
            try:
                hist = History.objects.get(classify='RC', model_id=recipe.id)
                hist.update_time = timezone.now()
                hist.save()
                print("save : ", hist.update_time, "   -   ", timezone.now())
                print(e)
            except Exception as e:
                print(e)
        finally:
            print("----------------")
            return Response(data)


class CommentDetailAPIView(RetrieveUpdateDestroyAPIView):
    serializer_class = CommentSerializer
    lookup_field = "comment_id"

    def get_queryset(self):
        return Comment.objects.get(id=self.kwargs['comment_id'])

    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def put(self, request, *args, **kwargs):
        return super().put(request, *args, **kwargs)

    def patch(self, request, *args, **kwargs):
        return super().patch(request, *args, **kwargs)

    def delete(self, request, *args, **kwargs):
        id = self.kwargs['comment_id']
        comment = Comment.objects.get(id=id)
        comment.delete()
        try:
            histories = History.objects.filter(Q(classify='CL', model_id=id)
                                               | Q(classify='RC', model_id=id))
            for hist in histories:
                hist.delete()
        except:
            pass
        print(self.kwargs)
        return Response(False)


class SearchContainerList(ListCreateAPIView):
    # queryset = RecipeContainer.objects.filter(user_id=2)
    permission_classes = [
        permissions.AllowAny
    ]
    serializer_class = SimpleRecipeContainerSerializer

    # 여기에서 유저별로 다른 홈 아이템들을 뿌릴 수 있게
    def get_queryset(self):
        user_id = self.kwargs['user_id']
        order = int(self.request.GET['order'])
        user = User.objects.get(id=user_id)
        count = RecipeContainer.objects.all().count()
        # 본인거 + follow한 유저거
        # user에서 favorite_food 목록 꺼낸담에 RecipeContainer 목록으로 추가하기
        start = order * 5
        end = start + 5
        # follower = user.likes_for_container
        fav = user.favorite_food.split(",")
        # print(follower)
        try:
            recipe_container_object = RecipeContainer.objects.filter(Q(user=user) & ~Q(user=user))
            for fav_item in fav:
                additional_object = RecipeContainer.objects.filter(~Q(user=user) & Q(category=fav_item)).order_by(
                    '-created_date')
                print(fav_item)
                print(additional_object, "-additional")
                print(recipe_container_object.count(), "-count")
                if recipe_container_object.count() < 1:
                    recipe_container_object = additional_object
                else:
                    recipe_container_object = recipe_container_object | additional_object
                print(recipe_container_object, "-recipe_obj\n\n")
            additional_object = RecipeContainer.objects.filter(Q(type=False)).order_by('-hit_count')
            recipe_container_object = recipe_container_object | additional_object
            recipe_container_object = recipe_container_object.order_by('-hit_count')[start:end]
        except Exception as e:
            print(e)
        finally:
            print(recipe_container_object)
            return recipe_container_object

    def get(self, request, *args, **kwargs):
        print("\nrequest : ", request.body)
        return super().get(request, *args, **kwargs)

    def post(self, request, *args, **kwargs):
        print("\nkwargs : ", request.body)
        return super().post(request, *args, **kwargs)


class SearchStoryList(ListCreateAPIView):
    serializer_class = SearchStorySerializer

    def get_queryset(self):
        get_query_set = self.request.GET
        search_text = get_query_set['query']
        if search_text != "":
            return RecipeContainer.objects.filter(introduce_recipe__icontains=search_text, type=False).order_by(
                '-hit_count')
        return None


class SearchRecipeList(ListCreateAPIView):
    serializer_class = SearchReferenceSerializer

    def get_queryset(self):
        get_query_set = self.request.GET
        search_text = get_query_set['query'].split()
        result = RecipeContainer.objects.filter(~Q(id=1) & Q(id=1))
        for text in search_text:
            new_result = RecipeContainer.objects.filter(food_name__icontains=text, type=True) | \
                   RecipeContainer.objects.filter(recipe_title__icontains=text, type=True).order_by('-hit_count')
            if new_result.first() is not None:
                result = result | new_result
        return result


class SearchReferenceList(ListCreateAPIView):
    serializer_class = SearchReferenceSerializer

    def get_queryset(self):
        get_query_set = self.request.GET
        print("kwargs : ", self.kwargs)
        print("args : ", self.args)
        search_text = get_query_set['searchText']
        recipe_container_object = RecipeContainer.objects.filter(food_name__icontains=search_text, type=True) | \
                                  RecipeContainer.objects.filter(recipe_title__icontains=search_text,
                                                                 type=True).order_by('-created_date')
        return recipe_container_object


class MyReferenceList(ListCreateAPIView):
    serializer_class = SearchReferenceSerializer

    def get_queryset(self):
        get_query_set = self.request.GET
        print("kwargs : ", self.kwargs)
        print("args : ", self.args)
        user = User.objects.get(id=self.kwargs['user_id'])
        recipe_container_object = user.recipe_container.filter(type=True).order_by('-created_date')
        return recipe_container_object
