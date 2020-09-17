import base64
import json
from django.utils import timezone
from recipeApi.models import RecipeContainer, Recipe, MultiImageResult, Ingredients, ExtraTip, History
from userApi.models import User


def post_register_result(req, user_id):
    data = req.POST['recipeContainer']
    data = json.loads(data)
    extra_tips = data['extra_tip']
    ingredients = data['ingredients']
    recipes = data['recipes']
    recipe_container = data['recipe_container']
    images = req.FILES
    recipe_images = []
    complete_images = []
    print(data)
    print(images)
    for image in images:
        kind = image.split("_")[0]
        if kind == "recipe":
            recipe_images.append(images[image])
        elif kind == "complete":
            complete_images.append(images[image])
    print("comple Img : ", complete_images)
    try:
        user = User.objects.get(id=user_id)
        container = RecipeContainer.objects.create(user=user,
                                                   recipe_title=recipe_container['recipe_title'],
                                                   food_name=recipe_container['food_name'],
                                                   introduce_recipe=recipe_container['introduce_recipe'],
                                                   category=recipe_container['category'],
                                                   for_person=recipe_container['for_person'],
                                                   required_time=recipe_container['required_time'], type=True)
        for tip in extra_tips:
            tip_object = ExtraTip.objects.create(recipe_container=container, tip=tip['tip'])
            tip_object.save()
        for ingredient in ingredients:
            Ingredients.objects.create(recipe_container=container, ingredient=ingredient['ingredient'],
                                       amount=ingredient['amount'])
        for recipe in range(0, len(recipes)):
            Recipe.objects.create(recipe_container=container, order=recipes[recipe]['order'],
                                  image=recipe_images[recipe], explain=recipes[recipe]['explain'])
        for result_img in complete_images:
            print(result_img)
            MultiImageResult.objects.create(recipe_container=container, image=result_img)
        return True
    except Exception as e:
        print(e)
        return False


def edit_register_result(req, recipe_id, user_id):
    data = req.POST['recipeContainer']
    data = json.loads(data)
    extra_tips = data['extra_tip']
    ingredients = data['ingredients']
    recipes = data['recipes']
    recipe_container = data['recipe_container']
    images = req.FILES
    recipe_images = []
    complete_images = []
    recipe = RecipeContainer.objects.get(id=recipe_id)
    for image in images:
        kind = image.split("_")[0]
        if kind == "recipe":
            recipe_images.append(images[image])
        elif kind == "complete":
            complete_images.append(images[image])

    if len(complete_images) > 0:
        multi_photos = recipe.multi_image_result.all()
        for multi in multi_photos:
            multi.delete()
    ingre = recipe.ingredients.all()
    for ing in ingre:
        ing.delete()
    extra_tip = recipe.extra_tip.all()
    for ext in extra_tip:
        ext.delete()
    recipe_order = recipe.recipe_order.all()
    for rec in recipe_order:
        rec.delete()
    try:
        user = User.objects.get(id=user_id)
        recipe.user = user
        recipe.recipe_title = recipe_container['recipe_title']
        recipe.food_name = recipe_container['food_name']
        recipe.introduce_recipe = recipe_container['introduce_recipe']
        recipe.category = recipe_container['category']
        per = int(recipe_container['for_person'])
        recipe.for_person = per
        req = int(recipe_container['required_time'])
        recipe.required_time = req
        recipe.save()
        for tip in extra_tips:
            tip_object = ExtraTip.objects.create(recipe_container=recipe, tip=tip['tip'])
            tip_object.save()
        for ingredient in ingredients:
            Ingredients.objects.create(recipe_container=recipe, ingredient=ingredient['ingredient'],
                                       amount=ingredient['amount'])

        print(recipes)
        print(recipe_images)
        print(len(recipes))
        for i in range(0, len(recipes)):
            Recipe.objects.create(recipe_container=recipe, order=recipes[i]['order'],
                                  image=recipe_images[i], explain=recipes[i]['explain'])
        if len(complete_images) > 0:
            for result_img in complete_images:
                MultiImageResult.objects.create(recipe_container=recipe, image=result_img)
            return True
    except Exception as e:
        print(e)
        return False


def edit_story_result(req, recipe_id, user_id):
    data = req.POST['story_container']
    recipe_container = json.loads(data)
    references = recipe_container['references']
    images = req.FILES
    complete_images = []
    recipe = RecipeContainer.objects.get(id=recipe_id)
    for image in images:
        kind = image.split("_")[0]
        if kind == "complete":
            complete_images.append(images[image])
    if len(complete_images) > 0:
        multi_photos = recipe.multi_image_result.all()
        for multi in multi_photos:
            multi.delete()

    for ref in references:
        try:
            original_recipe = RecipeContainer.objects.get(id=ref)
            original_recipe.reference.add(recipe)
            History.objects.create(classify='RR', model_id=ref, user=original_recipe.user)
        except Exception as e:
            print(e)
    try:
        user = User.objects.get(id=user_id)
        recipe.user = user
        recipe.introduce_recipe = recipe_container['content']
        recipe.save()
        if len(complete_images) > 0:
            for result_img in complete_images:
                MultiImageResult.objects.create(recipe_container=recipe, image=result_img)
            return True
    except Exception as e:
        print(e)
        return False


def post_register_story(req, user_id):
    data = req.POST['story_container']
    data = json.loads(data)
    story_content = data['content']
    references = data['references']
    images = req.FILES
    try:
        user = User.objects.get(id=user_id)
        container = RecipeContainer.objects.create(user=user,
                                                   introduce_recipe=story_content)
        for ref in references:
            try:
                print(ref)
                original_recipe = RecipeContainer.objects.get(id=ref)
                original_recipe.reference.add(container)
                History.objects.create(classify='RR', model_id=ref, user=original_recipe.user)
            except Exception as e:
                history = History.objects.get(classify='RR', model_id=ref, user=original_recipe.user)
                history.update_time = timezone.now()
                history.save()
                print(e)
        for img in images.values():
            MultiImageResult.objects.create(recipe_container=container, image=img)
        return True
    except Exception as e:
        print(e)
        return False


def get_single_recipe(recipe_id):
    container = RecipeContainer.objects.get(id=recipe_id)
    return container


def sort_main_item(user_id):
    result = []
    user = User.objects.get(id=user_id)
    item_list = RecipeContainer.objects.filter()
    favorite_food = user.favorite_food.split(",")
    # recipeList = user.recipecontainer_set.all()
    recipe_list = RecipeContainer.objects.all()
    for recipe in recipe_list:
        print(recipe.user.id, user_id)
        try:
            user.likes_for_container.get(id=recipe.pk)
            is_like = True
        except Exception as e:
            is_like = False
            print(e)

        print("isLike : ", recipe.comment_set.count())
        # user_list = [recipe.user]
        # user_json = serializers.serialize('json', user_list)
        # print(user_json)
        user_item = recipe.user
        try:
            image_url = user_item.profile_image.url
        except Exception as e:
            image_url = None
            print(e)
        user_json = {'id': user_item.id, 'user_id': user_item.user_id, 'nickname': user_item.nickname,
                     'profile_image': image_url}

        temp = {'user': user_json, 'type': recipe.type, 'image_list': None, 'content': recipe.introduce_recipe,
                'total_likes': recipe.total_likes(), 'is_like': is_like, 'total_comment': recipe.comment_set.count(),
                'has_in_list': False}
        result.append(temp)
    # print(user.user_set.all())
    # print(recipe_list)
    # print(user, "\n", favorite_food)
    # print(result)
    # print(result)
    return result


def like_dislike(flag, recipe, to_obj):
    from_obj_what = recipe.likes
    print("flag : ", flag)
    try:
        History.objects.create(classify='RL', model_id=recipe.id, user=recipe.user)
        print("create")
    except Exception as e:
        print(e)
        print("create2")
        try:
            hist = History.objects.get(classify='RL', model_id=recipe.id)
            hist.update_time = timezone.now()
            hist.save()
        except Exception as e:
            print(e)
            return None
    finally:
        if flag:
            try:
                from_obj_what.add(to_obj)
            except Exception as e:
                print(e)
            return flag
        likes = from_obj_what.all()
        for obj in likes:
            if obj == to_obj:
                print("좋아요 취소")
                from_obj_what.remove(obj)
                try:
                    hist = History.objects.get(model_id=recipe.id, classify='RL')
                    if recipe.likes.count() == 0:
                        hist.delete()
                except Exception as e:
                    print(e)
                return flag
    return None
