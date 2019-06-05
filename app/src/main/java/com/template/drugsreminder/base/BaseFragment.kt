package com.template.drugsreminder.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.MenuRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptions

abstract class BaseFragment : Fragment() {

    private val config = Config()

    protected fun <Model : ViewModel> getViewModel(modelClass: Class<Model>): Model? {
        val model =
            ViewModelProviders.of(activity!!).get<ModelProviderViewModel>(ModelProviderViewModel::class.java)
                .takeModel()
        return if (model != null && modelClass.isAssignableFrom(model!!.javaClass)) {
            ViewModelProviders.of(this, ViewModelFactory(model)).get(modelClass)
        } else {
            try {
                ViewModelProviders.of(this).get(modelClass)
            } catch (e: Throwable) {
                Log.e(
                    "NAVIGATION",
                    modelClass.canonicalName!! + "should be provided via \"navController.navigate(resId, activity, model)\""
                )
                null
            }
        }
    }


    class ModelProviderViewModel : ViewModel() {
        private var internalModel: ViewModel? = null

        fun takeModel(): ViewModel? {
            val result = internalModel
            internalModel = null
            return result
        }

        fun setModel(data: ViewModel) {
            this.internalModel = data
        }
    }


    internal inner class ViewModelFactory<Model : ViewModel>(private val model: Model) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <ModelClass : ViewModel> create(modelClass: Class<ModelClass>): ModelClass {
            return model as ModelClass
        }
    }

    protected fun getNavController(): FragmentNavigationController {
        return FragmentNavigationController(this)
    }

    private fun getNavHost(): NavHost {
        return activity as NavHost
    }


    protected inner class FragmentNavigationController internal constructor(private val fragment: Fragment) {

        private val rawController: NavController
            get() = getNavHost().getNavController()

        @JvmOverloads
        fun navigate(@IdRes resId: Int, model: ViewModel? = null, args: Bundle? = null, options: NavOptions? = null) {
            if (fragment.activity == null)
                throw RuntimeException("Navigation not allowed here (activity is null / fragment not attached)")
            if (model != null)
                ViewModelProviders.of(fragment.activity!!).get(ModelProviderViewModel::class.java).setModel(model)
            Log.d("NAVIGATION", fragment.javaClass.simpleName)
            rawController.navigate(resId, args, options, null)
        }

        fun navigate(@IdRes resId: Int, options: NavOptions) {
            navigate(resId, null, null, options)
        }

        fun navigate(@IdRes resId: Int, args: Bundle) {
            navigate(resId, null, args, null)
        }

        fun navigate(@IdRes resId: Int, model: ViewModel, options: NavOptions) {
            navigate(resId, model, null, options)
        }

        fun navigateUp() {
            rawController.navigateUp()
        }
    }

    protected fun getConfig(): Config {
        return config
    }

    inner class Config {

        private var isBottomBarVisible = true

        fun apply() {
            getNavHost().setBottomBarVisibility(isBottomBarVisible)
        }


        fun setBottomBarVisible(bottomBarVisible: Boolean): Config {
            isBottomBarVisible = bottomBarVisible
            return this
        }

    }
}

