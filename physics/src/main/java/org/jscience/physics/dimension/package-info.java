/**
 This package provides support for <a href="http://en.wikipedia.org/wiki/Dimensional_analysis">Dimensional Analysis</a>.
 
 <p> The difference between dimensional models lies in the assumptions each makes and,
    in consequence,the operations each permits. For example, the summation
    of a {@link org.unitsofmeasurement.quantity.Length length} and a {@link org.unitsofmeasurement.quantity.Duration duration}
    is not allowed by the standard model, but is quite valid in a relativistic context.</p>
    
<p> Models are {@link javolution.context.LocalContext context-local}, allowing 
    multiple models to be used concurrently. For example:[code]
        LocalContext.enter();
        try {
            RelativisticModel.select(); // Affects the current thread only.
            ...
        } finally {
           LocalContext.exit();
        }[/code]</p>

<p> The names and characteristics of the models are presented in the following table:</p>

<table border="1" cellspacing="1">
  <tr align="center" valign="bottom">
    <th>	Model</th>
    <th>	Class</th>
    <th>	Defining Characteristics</th>
    <th>	Default Output Units</th>
  </tr>

  <tr align="left" valign="middle">
    <td align="left">	Standard</td>
    <td align="left">	<samp>"StandardModel"</samp></td>
    <td align="left">	per Syst&egrave;me Internationale</td>
    <td align="left">	<samp>Length</samp>:&nbsp;<i>m</i>;&nbsp;

			<samp>Mass</samp>:&nbsp;<i>kg</i>;&nbsp;
			<samp>Duration</samp>:&nbsp;<i>s</i>;&nbsp;
			<samp>ElectricCurrent</samp>:&nbsp;<i>A</i>;&nbsp;

			<samp>Temperature</samp>:&nbsp;<i>K</i>;&nbsp;
			<samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
			<samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>

    </td>
  </tr>

  <tr align="left" valign="middle">
    <td align="left">	Relativistic</td>
    <td align="left">	<samp>"RelativisticModel"</samp></td>
    <td align="left">	1 <i>= c</i></td>
    <td align="left">	<samp>Length</samp>,
    			<samp>Duration</samp>:&nbsp;<i>s</i>;&nbsp;
			<samp>Mass</samp>:&nbsp;<i>eV</i>;&nbsp;
			<samp>ElectricCurrent</samp>:&nbsp;<i>A</i>;&nbsp;

			<samp>Temperature</samp>:&nbsp;<i>K</i>;&nbsp;
			<samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
			<samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>

    </td>
  </tr>

  <tr align="left" valign="middle">
    <td align="left">	High-Energy</td>
    <td align="left">	<samp>"HighEnergyModel"</samp></td>
    <td align="left">	1<i> = c<br>
			&nbsp;&nbsp; = k<br>
			&nbsp;&nbsp; = ePlus</i>
    </td>
    <td align="left">	<samp>Length</samp>,
    			<samp>Duration</samp>:&nbsp;<i>ns</i>;&nbsp;
			<samp>Mass</samp>,
    			<samp>Temperature</samp>:&nbsp;<i>GeV</i>;&nbsp;

			<samp>ElectricCurrent</samp>:&nbsp;<i>1/ns</i>;&nbsp;
			<samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
			<samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>
    </td>
  </tr>

  <tr align="left" valign="middle">
    <td align="left">	Quantum</td>
    <td align="left">	<samp>"QuantumModel"</samp></td>
    <td align="left">	1<i> = c<br>
			&nbsp;&nbsp; = k<br>
			&nbsp;&nbsp; = µ0<br>
			&nbsp;&nbsp; = hBar</i>
    </td>
    <td align="left">	<samp>Length</samp>,
    			<samp>Duration</samp>:&nbsp;<i>1/GeV</i>;&nbsp;
			<samp>Mass</samp>,
    			<samp>Temperature</samp>,
			<samp>ElectricCurrent</samp>:&nbsp;<i>GeV</i>;&nbsp;

			<samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
			<samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>
    </td>
  </tr>

  <tr align="left" valign="middle">
    <td align="left">	Natural</td>
    <td align="left">	<samp>"NaturalModel"</samp></td>
    <td align="left">	1<i> = c<br>
			&nbsp;&nbsp; = k<br>
			&nbsp;&nbsp; = µ0<br>
			&nbsp;&nbsp; = hBar<br>
			&nbsp;&nbsp; = G</i>
    </td>
    <td align="left">	<samp>Length</samp>,
			<samp>Mass</samp>,
			<samp>Duration</samp>,
			<samp>ElectricCurrent</samp>,
			<samp>Temperature</samp>:&nbsp;1;&nbsp;
			<samp>AmountOfSubstance</samp>:&nbsp;<i>mol</i>;&nbsp;
			<samp>LuminousIntensity</samp>:&nbsp;<i>cd</i>
    </td>
  </tr>
</table>
 **/
package org.jscience.physics.dimension;
