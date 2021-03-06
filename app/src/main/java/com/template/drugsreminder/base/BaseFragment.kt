package com.template.drugsreminder.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlin.reflect.KClass

abstract class BaseFragment : androidx.fragment.app.Fragment() {

    protected val config = Config()

    protected fun <Model : ViewModel> getViewModel(modelClass: KClass<Model>): Model? {
        val model =
            ViewModelProviders.of(activity!!).get<ModelProviderViewModel>(ModelProviderViewModel::class.java)
                .takeModel()
        return if (model != null && modelClass.java.isAssignableFrom(model!!.javaClass)) {
            ViewModelProviders.of(this, ViewModelFactory(model)).get(modelClass.java)
        } else {
            try {
                ViewModelProviders.of(this).get(modelClass.java)
            } catch (e: Throwable) {
                Log.e(
                    "NAVIGATION",
                    modelClass.java.canonicalName!! + "should be provided via \"navController.navigate(resId, activity, model)\""
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


    protected inner class FragmentNavigationController internal constructor(private val fragment: androidx.fragment.app.Fragment) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config.apply()
    }

    inner class Config {

        var isBottomBarVisible = true

        fun apply() {
            getNavHost().setBottomBarVisibility(isBottomBarVisible)
        }
    }
}

